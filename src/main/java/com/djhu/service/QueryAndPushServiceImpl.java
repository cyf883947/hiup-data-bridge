package com.djhu.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.common.constant.PurchaserStatusConstant;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.elasticsearch.core.ElasticsearchTemplate;
import com.djhu.elasticsearch.core.handle.MsgHandle;
import com.djhu.elasticsearch.core.request.*;
import com.djhu.entity.MsgInfo;
import com.djhu.entity.ResultEntity;
import com.djhu.entity.atses.TbDbPurchaser;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.entity.atses.TbPatientuniqueidBackups;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.service.push.ProvideAndSendData;
import com.djhu.service.query.IQueryDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 17:15
 **/
@Slf4j
@Service
public class QueryAndPushServiceImpl implements IQueryAndPushService {
    /**
     * 数据库的可用状态 2-为已完成创建的数据库
     */
    String DATASOURCE_ENABLED_STATUS = "2";
    /**
     * 全院库的状态，推送数据不推送全院库数据
     */
    String QUAN_YUAN_KU_STATUS = "1";


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
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public void dispose(String dbId, MsgInfo msgInfo, Integer pushType) throws Exception {
        List<String> dbIds = getAvailableDataSource(dbId);
        for (String resourceDbId : dbIds) {

            // 取所有厂商
            Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId);
            for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
                String purchasersId = entry.getKey();
                String purchasersUrl = entry.getValue();

                SearchRequest searchRequest = null;
                if (ProvideAndSendData.ALL.equals(pushType)) {
                    if (isFirst(purchasersId, resourceDbId)) {
                        searchRequest = new MacthAllRequest();
                    }
                } else if (ProvideAndSendData.ADD.equals(pushType)) {
                    if (isFirst(purchasersId, resourceDbId)) {
                        searchRequest = new MacthAllRequest();
                    } else {
                        searchRequest = getSearchRequest(resourceDbId, purchasersId);
                    }
                } else {

                    Object obj = queryDataService.findById(msgInfo.getIndex(), msgInfo.getType(), msgInfo.getId());
                    if(obj == null){
                        return;
                    }
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String patientUniqueId = String.valueOf(map.getOrDefault("patient_unique_id", ""));

                    EntityWrapper<TbPatientuniqueidBackups> wrapper = new EntityWrapper<>();
                    wrapper.eq("DB_ID",resourceDbId);
                    wrapper.eq("PATIENT_UNIQUE_ID",patientUniqueId);
                    List<TbPatientuniqueidBackups> tbPatientuniqueidBackups = patientuniqueidBackupsService.selectList(wrapper);
                    if(CollectionUtils.isNotEmpty(tbPatientuniqueidBackups) && isPushed(tbPatientuniqueidBackups.get(0).getId(),resourceDbId,purchasersId)){
                        log.info("此条数据推送过，暂不处理!!!");
                    }else {
                        // 推送单条记录
                        pushDataAndUpdateStatus(purchasersId, purchasersUrl, map, resourceDbId);
                    }
                }

                String esSuffix = tbDbResourceService.getEsSuffixByDbId(resourceDbId);
                if (searchRequest == null || StringUtils.isEmpty(esSuffix)) {
                    return;
                }
                ((MacthAllRequest) searchRequest).setIndex(GlobalConstant.HIUP_PERSON_INDEX + "_" + esSuffix);
                ((MacthAllRequest) searchRequest).setType(GlobalConstant.HIUP_PERSON_TYPE);
                long total = elasticsearchTemplate.count(searchRequest);
                if (total > 0) {
                    long startTime = System.currentTimeMillis();
                    elasticsearchTemplate.queryForHandle(searchRequest, new MsgHandle() {
                        @Override
                        public void handle(Object o) {
                            Map<String, Object> map = (Map<String, Object>) o;
                            pushDataAndUpdateStatus(purchasersId, purchasersUrl, map, resourceDbId);
                        }
                    });
                    long consume = System.currentTimeMillis() - startTime;
                    log.info("推送厂商数据成功!!! 推送数据量为：{} 条,耗时：{} ms", total, consume);
                } else {
                    log.info("需要推送的数据为空,不用处理!!!");
                }
            }
        }
    }

    private boolean isPushed(String id, String resourceDbId, String purchasersId) {
        EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("BAK_ID",id);
        wrapper.eq("EXTERNAL_DB_ID",purchasersId);
        wrapper.eq("PURCHASER_ID",resourceDbId);
        int count = purchaserRecordService.selectCount(wrapper);
        return count>0;
    }

    private SearchRequest getSearchRequest(String resourceDbId, String purchasersId) {
        SearchRequest searchRequest = null;

        EntityWrapper<TbDbPurchaserRecord> recordEntityWrapper = new EntityWrapper<>();
        recordEntityWrapper.eq("EXTERNAL_DB_ID", resourceDbId);
        recordEntityWrapper.eq("PURCHASER_ID", purchasersId);
        recordEntityWrapper.setSqlSelect("BAK_ID");
        List<TbDbPurchaserRecord> tbDbPurchaserRecords = purchaserRecordService.selectList(recordEntityWrapper);
        Set<String> bakIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(tbDbPurchaserRecords)) {
            tbDbPurchaserRecords.forEach(record -> {
                if (!bakIdSet.contains(record.getBakId())) {
                    bakIdSet.add(record.getBakId());
                }
            });
        }
        EntityWrapper<TbPatientuniqueidBackups> wrapper = new EntityWrapper<>();
        wrapper.eq("DB_ID", resourceDbId);
        wrapper.notIn("ID",bakIdSet);
        List<TbPatientuniqueidBackups> backups2 = patientuniqueidBackupsService.selectList(wrapper);
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

    private boolean isFirst(String purchasersId, String resourceDbId) {
        EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("EXTERNAL_DB_ID", resourceDbId);
        wrapper.eq("PURCHASER_ID", purchasersId);
        int count = purchaserRecordService.selectCount(wrapper);
        return count == 0;
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
        log.info("所有可用数据库ids：{}", dbIds);
        return dbIds;
    }


    private void pushDataAndUpdateStatus(String purchasersId, String purchasersUrl, Map<String, Object> map, String resourceDbId) {
        long startTime = System.currentTimeMillis();
        String patientUniqueId = String.valueOf(map.getOrDefault("", ""));

        try {
            log.info("开始调用厂商接口执行推送, 厂商id：{}，厂商地址：{}", purchasersId, purchasersUrl);

            Object o = JSON.toJSON(map);
            JSONObject json = JSON.parseObject(o.toString());

            ResponseEntity<ResultEntity> response = restTemplate.postForEntity(purchasersUrl, json, ResultEntity.class);
            // 先设定一个模拟返回实体
            ResultEntity result = response.getBody();
            log.info("返回结果为：{} ", result);

            long consume = System.currentTimeMillis() - startTime;
            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(resourceDbId, purchasersId, PushStatusConstant.SUCCESS, consume, patientUniqueId);
            purchaserRecordService.insert(purchaserRecord);
        } catch (RestClientException e) {
            log.error("推送厂商数据失败!!! 厂商id：{}, 厂商url: {}", purchasersId, purchasersUrl);
            long consume = System.currentTimeMillis() - startTime;
            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(resourceDbId, purchasersId, PushStatusConstant.ERROR, consume, patientUniqueId);
            purchaserRecordService.insert(purchaserRecord);
            return;
        }
    }

    /**
     * 构建厂商推送数据记录实体
     *
     * @param dbId            当前推送的专科库id
     * @param purchaserId     厂商id
     * @param pushStatus      推送数据状态 0-成功 1-失败
     * @param consume
     * @param patientUniqueId 患者编号，记录哪条记录被推送
     * @return
     */
    private TbDbPurchaserRecord getTbDbPurchaserRecord(String dbId, String purchaserId, String pushStatus, long consume, String patientUniqueId) {
        TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
        // todo 改下这个bakId
        purchaserRecord.setBakId(null);
        purchaserRecord.setExternalDbId(dbId);
        purchaserRecord.setConsume(String.valueOf(consume));
        purchaserRecord.setPurchaserId(purchaserId);
        purchaserRecord.setStatus(pushStatus);
        return purchaserRecord;
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
        log.info("当前需要推送厂商列表:{}, 数据库dbId: {}", purchasersMap, dbId);
        return purchasersMap;
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
     * 获取所有可用的厂商
     *
     * @return 可用的厂商名称和调用地址
     */
    private Map<String, String> getPurchasersMap() {
        EntityWrapper<TbDbPurchaser> purchaserEntityWrapper = new EntityWrapper<>();
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
        log.info("当前可用厂商列表:{}", purchasersMap);
        return purchasersMap;
    }


}
