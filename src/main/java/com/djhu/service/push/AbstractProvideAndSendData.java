package com.djhu.service.push;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.common.constant.PatientStatusConstant;
import com.djhu.common.constant.PurchaserStatusConstant;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.elasticsearch.core.request.BoolSearchRequest;
import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.elasticsearch.core.request.ShouldSearchRequest;
import com.djhu.elasticsearch.core.request.TermSearchRequest;
import com.djhu.entity.MsgInfo;
import com.djhu.entity.atses.TbDbPurchaser;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.entity.atses.TbPatientuniqueidBackups;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.service.*;
import com.djhu.service.query.IQueryDataService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 9:51
 **/
@Slf4j
public abstract class AbstractProvideAndSendData implements ProvideAndSendData {

    @Autowired
    ITbDbPurchaserService purchaserService;
    @Autowired
    ITbDbResourceService tbDbResourceService;
    @Autowired
    ITbDbPurchaserRecordService purchaserRecordService;
    @Autowired
    ITbPatientuniqueidBackupsService patientuniqueidBackupsService;
    @Autowired
    IQueryDataService queryDataService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void push(Integer pushType) {
        push(null, pushType);
    }

    @Override
    public void push(String dbId, Integer pushType) {
        push(dbId, null, pushType);
    }

    @Override
    public void push(String dbId, MsgInfo msgInfo, Integer pushType) {
        List<String> dbIds = getAvailableDataSource(dbId);
        for (String resourceDbId : dbIds) {

            if (isProduce(resourceDbId)) {
                // 获取需要推送的数据并添加字段
                List list = addFieldToList(produce(resourceDbId, pushType, msgInfo), resourceDbId);

                Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId);
                for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
                    String purchasersId = entry.getKey();
                    String purchasersUrl = entry.getValue();
                    doPushAndDispose(list, purchasersId, purchasersUrl, resourceDbId);
                }
            }
        }
    }


    private List addFieldToList(List list, String resourceDbId) {
        if (CollectionUtils.isNotEmpty(list)) {
            List resultList = new LinkedList();
            for (Object obj : list) {
                Object o = JSON.toJSON(obj);
                JSONObject json = JSON.parseObject(o.toString());
                json.put("db_id", resourceDbId);
                // 目前数据的状态都为新增
                json.put("data_status", PatientStatusConstant.ADD);
                resultList.add(json);
            }
            return resultList;
        }
        return list;
    }

    protected abstract List produce(String dbId, Integer pushType, MsgInfo msgInfo);

    protected abstract boolean isProduce(String dbId);


    private void doPushAndDispose(List list, String purchasersId, String purchasersUrl, String dbId) {
        // 调用接口 执行推送
        long startTime = System.currentTimeMillis();
        long consume = 0;
        ResultEntity result = null;
        long total = 0;
        try {
            ResponseEntity<ResultEntity> response = restTemplate.postForEntity(purchasersUrl, list, ResultEntity.class);
            // 先设定一个模拟返回实体
            result = response.getBody();
            total = CollectionUtils.isNotEmpty(list) ? list.size() : 0;
            consume = System.currentTimeMillis() - startTime;
            log.info("推送厂商数据成功!!! 厂商id: {} 推送数据量为：{} 条", purchasersId, total);
            log.info("返回结果为：{} 耗时：{} ms", result, consume);
        } catch (RestClientException e) {
            log.error("推送厂商数据失败!!! 厂商id：{}, 厂商url: {}", purchasersId, purchasersUrl);
            // todo 推送失败的怎么处理
        }

        afterDispose(result, purchasersId, dbId, total, consume);
    }

    private void afterDispose(ResultEntity result, String purchasersId, String dbId, long total, long consume) {
        try {
            doAfterDispose(result, purchasersId, dbId, total, consume);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("推送完成后执行后续操作失败!!! result: {},{}", result, e);
        }
    }

    // todo 添加事物
    private void doAfterDispose(ResultEntity result, String purchasersId, String dbId, long total, long consume) {
        // 成功后
        if (result.getStatus() == 200) {
            /*
            1. 删除 bak 表中状态为1的数据
            2. 设置 bak 表中状态为2的数据改为1
            3. 记录 record 厂商推送记录到表
             */
            EntityWrapper<TbPatientuniqueidBackups> backupsEntityWrapper = new EntityWrapper<>();
            backupsEntityWrapper.eq("DB_ID", dbId);
            backupsEntityWrapper.eq("STATUS", PushStatusConstant.PUSHED);
            patientuniqueidBackupsService.delete(backupsEntityWrapper);

            backupsEntityWrapper.eq("STATUS", PushStatusConstant.NOT_PUSHED);
            List<TbPatientuniqueidBackups> backupsList = patientuniqueidBackupsService.selectList(backupsEntityWrapper);
            patientuniqueidBackupsService.insertOrUpdateBatch(backupsList);

            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(dbId, purchasersId, PushStatusConstant.SUCCESS, total, consume);
            purchaserRecordService.insert(purchaserRecord);
        } else {
            // 记录 record 厂商推送失败状态，执行重试
            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(dbId, purchasersId, PushStatusConstant.ERROR, total, consume);
            purchaserRecordService.insert(purchaserRecord);
        }
    }


    /**
     * 构建厂商推送数据记录
     *
     * @param dbId        当前推送的专科库id
     * @param purchaserId 厂商id
     * @param pushStatus  推送数据状态 0-成功 1-失败
     * @param total
     * @param consume
     * @return
     */
    private TbDbPurchaserRecord getTbDbPurchaserRecord(String dbId, String purchaserId, String pushStatus, long total, long consume) {
        TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
        purchaserRecord.setCurrentDbId(dbId);
        purchaserRecord.setExternalDbId(dbId);
        purchaserRecord.setConsume(String.valueOf(consume));
        purchaserRecord.setPurchaserId(purchaserId);
        purchaserRecord.setStatus(pushStatus);
        purchaserRecord.setTotal(String.valueOf(total));
        return purchaserRecord;
    }

    protected boolean existRecord(String dbId) {
        return CollectionUtils.isNotEmpty(getPushPurchasersRecordList(dbId));
    }

    private List<TbDbPurchaserRecord> getPushPurchasersRecordList(String dbId) {
        List<TbDbPurchaserRecord> tbDbPurchaserRecords = null;
        try {
            EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
            wrapper.eq("EXTERNAL_DB_ID", dbId);
            tbDbPurchaserRecords = purchaserRecordService.selectList(wrapper);
        } catch (Exception e) {
            log.error("查询专科库是否推送过失败!!! 数据库dbId : {},{}", dbId);
        }
        return tbDbPurchaserRecords;
    }

    /**
     * 获取所有可用的数据库ids
     *
     * @param dbId 数据库id
     * @return 所有可用的数据库id
     */
    protected List<String> getAvailableDataSource(String dbId) {
        List<String> dbIds = new ArrayList<>();
        if (StringUtils.isEmpty(dbId)) {
            dbIds = tbDbResourceService.selectDbIds();
        } else {
            // 判断该数据库是否可用或者是全院库
            TbDbResource tbDbResource = tbDbResourceService.selectById(dbId);
            if (tbDbResource == null || !DATASOURCE_ENABLED_STATUS.equalsIgnoreCase(tbDbResource.getStatus())
                    || QUAN_YUAN_KU_STATUS.equalsIgnoreCase(tbDbResource.getHospitalFlag())) {
                log.info("数据库不可用或是全院库!!! 数据库id：{}", dbId);
            } else {
                dbIds.add(dbId);
            }
        }
        return dbIds;
    }


    /**
     * 获取需要推送的厂商地址
     *
     * @param dbId
     * @return
     */
    private Map<String, String> getPushPurchasersMap(String dbId) {
        Map<String, String> purchasersMap = getPurchasersMap();

        List<TbDbPurchaserRecord> recordList = getPushPurchasersRecordList(dbId);
        if (CollectionUtils.isNotEmpty(recordList)) {
            Map<String, String> map = new HashMap<>(recordList.size());
            for (TbDbPurchaserRecord record : recordList) {
                String purchaserId = record.getPurchaserId();
                String status = record.getStatus();
                String url = purchasersMap.get(purchaserId);
                if (!purchasersMap.containsKey(purchaserId) && PushStatusConstant.SUCCESS.equalsIgnoreCase(status)) {
                    map.put(purchaserId, url);
                }
            }
            purchasersMap = map;
        }
        return purchasersMap;
    }

    /**
     * 获取所有可用的厂商
     *
     * @return 可用的厂商名称和调用地址
     */
    private Map<String, String> getPurchasersMap() {
        EntityWrapper<TbDbPurchaser> purchaserEntityWrapper = new EntityWrapper<>();
        purchaserEntityWrapper.eq("STATUS", PurchaserStatusConstant.ENABLED);
        purchaserEntityWrapper.eq("STATUS", PurchaserStatusConstant.ENABLED);
        // 厂商推送地址不能为空
        purchaserEntityWrapper.isNotNull("CUSTOM0");
        purchaserEntityWrapper.setSqlSelect(" id ", " CUSTOM0 ");
        List<TbDbPurchaser> tbDbPurchasers = purchaserService.selectList(purchaserEntityWrapper);
        Map<String, String> purchasersMap = new HashMap<>(tbDbPurchasers.size());
        for (TbDbPurchaser tbDbPurchaser : tbDbPurchasers) {
            String id = tbDbPurchaser.getId();
            String url = tbDbPurchaser.getCustom0();
            if (!purchasersMap.containsKey(id)) {
                purchasersMap.put(id, url);
            }
        }
        return purchasersMap;
    }


    protected SearchRequest getSearchRequest(String dbId) {
        SearchRequest searchRequest = null;
        EntityWrapper<TbPatientuniqueidBackups> wrapper = new EntityWrapper<>();
        wrapper.eq("DB_ID", dbId);
        wrapper.eq("STATUS", PushStatusConstant.PUSHED);
        List<TbPatientuniqueidBackups> backups1 = patientuniqueidBackupsService.selectList(wrapper);

        wrapper.eq("STATUS", PushStatusConstant.NOT_PUSHED);
        List<TbPatientuniqueidBackups> backups2 = patientuniqueidBackupsService.selectList(wrapper);
        backups2.removeAll(backups1);

        if (CollectionUtils.isNotEmpty(backups2)) {
            List<SearchRequest> searchRequests = new LinkedList<>();
            Set<String> pIdSet = new HashSet<>(backups2.size());
            for (TbPatientuniqueidBackups backups : backups2) {
                String patientUniqueId = backups.getPatientUniqueId();
                if (!pIdSet.contains(patientUniqueId)) {
                    TermSearchRequest termSearchRequest = new TermSearchRequest();
                    termSearchRequest.setField("patient_unique_id.keyword");
                    termSearchRequest.setValue(patientUniqueId);
                    searchRequests.add(new ShouldSearchRequest(termSearchRequest));
                    pIdSet.add(patientUniqueId);
                }
            }
            searchRequest = new BoolSearchRequest(searchRequests);
        }
        return searchRequest;
    }


    // 暂定厂商返回实体类
    @Data
    private class ResultEntity {
        private String msg;
        private Integer status = 200;
    }

}
