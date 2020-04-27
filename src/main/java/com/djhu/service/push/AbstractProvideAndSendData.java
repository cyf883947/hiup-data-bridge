//package com.djhu.service.push;
//
//import com.alibaba.dubbo.common.utils.CollectionUtils;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.djhu.common.constant.PatientStatusConstant;
//import com.djhu.common.constant.PurchaserStatusConstant;
//import com.djhu.common.constant.PushStatusConstant;
//import com.djhu.elasticsearch.core.request.BoolSearchRequest;
//import com.djhu.elasticsearch.core.request.SearchRequest;
//import com.djhu.elasticsearch.core.request.ShouldSearchRequest;
//import com.djhu.elasticsearch.core.request.TermSearchRequest;
//import com.djhu.entity.MsgInfo;
//import com.djhu.entity.atses.TbDbPurchaser;
//import com.djhu.entity.atses.TbDbPurchaserRecord;
//import com.djhu.entity.atses.TbPatientuniqueidBackups;
//import com.djhu.entity.scientper.TbDbResource;
//import com.djhu.service.*;
//import com.djhu.service.query.IQueryDataService;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
///**
// * @author cyf
// * @description
// * @create 2020-04-26 9:51
// **/
//@Slf4j
//public abstract class AbstractProvideAndSendData implements ProvideAndSendData {
//
//    @Autowired
//    ITbDbPurchaserService purchaserService;
//    @Autowired
//    ITbDbResourceService tbDbResourceService;
//    @Autowired
//    ITbDbPurchaserRecordService purchaserRecordService;
//    @Autowired
//    ITbPatientuniqueidBackupsService patientuniqueidBackupsService;
//    @Autowired
//    IQueryDataService queryDataService;
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Override
//    public void push(Integer pushType) {
//        push(null, pushType);
//    }
//
//    @Override
//    public void push(String dbId, Integer pushType) {
//        push(dbId, null, pushType);
//    }
//
//    @Override
//    public void push(String dbId, MsgInfo msgInfo, Integer pushType) {
//        List<String> dbIds = getAvailableDataSource(dbId);
//        for (String resourceDbId : dbIds) {
//
//            if (isProduce(resourceDbId)) {
//                // 获取需要推送的数据并添加字段
//                List list = addFieldToList(produce(resourceDbId, pushType, msgInfo), resourceDbId);
//
//                Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId);
//                for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
//                    String purchasersId = entry.getKey();
//                    String purchasersUrl = entry.getValue();
//                    doPushAndDispose(list, purchasersId, purchasersUrl, resourceDbId);
//                }
//            }
//        }
//    }
//
//
//    private List addFieldToList(List list, String resourceDbId) {
//        if (CollectionUtils.isNotEmpty(list)) {
//            List resultList = new LinkedList();
//            for (Object obj : list) {
//                Object o = JSON.toJSON(obj);
//                JSONObject json = JSON.parseObject(o.toString());
//                json.put("db_id", resourceDbId);
//                // 目前数据的状态都为新增
//                json.put("data_status", PatientStatusConstant.ADD);
//                resultList.add(json);
//            }
//            return resultList;
//        }
//        return list;
//    }
//
//    protected abstract List produce(String dbId, Integer pushType, MsgInfo msgInfo);
//
//    protected abstract boolean isProduce(String dbId);
//
//
//    private void doPushAndDispose(List list, String purchasersId, String purchasersUrl, String dbId) {
//        // 调用接口 执行推送
//        long startTime = System.currentTimeMillis();
//        long consume = 0;
//        long total = CollectionUtils.isNotEmpty(list) ? list.size() : 0;
//
//        try {
//            log.info("开始调用厂商接口执行推送, 厂商id：{}，厂商地址：{}", purchasersId, purchasersUrl);
//            ResponseEntity<ResultEntity> response = restTemplate.postForEntity(purchasersUrl, list, ResultEntity.class);
//            // 先设定一个模拟返回实体
//            ResultEntity result = response.getBody();
//            log.info("返回结果为：{} ", result);
//
//            afterDispose(result, purchasersId, dbId, total, consume);
//        } catch (RestClientException e) {
//            log.error("推送厂商数据失败!!! 厂商id：{}, 厂商url: {}", purchasersId, purchasersUrl);
//            // todo 推送失败的怎么处理
//        }
//
//        consume = System.currentTimeMillis() - startTime;
//        log.info("推送厂商数据成功!!! 推送数据量为：{} 条,耗时：{} ms", total, consume);
//    }
//
//    private void afterDispose(ResultEntity result, String purchasersId, String dbId, long total, long consume) {
//        try {
//            doAfterDispose(result, purchasersId, dbId, total, consume);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("推送完成后执行后续操作失败!!! result: {},{}", result, e);
//        }
//    }
//
//    // todo 添加事物
//    private void doAfterDispose(ResultEntity result, String purchasersId, String dbId, long total, long consume) {
//        // 成功后
//        if (result.getStatus() == 200) {
//            EntityWrapper<TbPatientuniqueidBackups> backupsEntityWrapper = new EntityWrapper<>();
////            backupsEntityWrapper.eq("DB_ID", dbId);
////            backupsEntityWrapper.eq("STATUS", PushStatusConstant.PUSHED);
////            patientuniqueidBackupsService.delete(backupsEntityWrapper);
////            log.info("删除 tb_patientuniqueid_bak 表中状态为0的数据成功!!!,数据库dbId : {}",dbId);
//
//            backupsEntityWrapper.eq("STATUS", PushStatusConstant.NOT_PUSHED).or().isNull("STATUS");
//            List<TbPatientuniqueidBackups> backupsList = patientuniqueidBackupsService.selectList(backupsEntityWrapper);
//            patientuniqueidBackupsService.insertOrUpdateBatch(backupsList);
//            log.info("更新 tb_patientuniqueid_bak 表中状态为1的数据为状态0成功!!!,数据库dbId : {}", dbId);
//
//            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(dbId, purchasersId, PushStatusConstant.SUCCESS, total, consume);
//            purchaserRecordService.insert(purchaserRecord);
//            log.info("保存推送成功记录日志到 TB_DB_PURCHASER_RECORD中成功!!!");
//        } else {
//            // 记录 record 厂商推送失败状态，执行重试
//            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(dbId, purchasersId, PushStatusConstant.ERROR, total, consume);
//            purchaserRecordService.insert(purchaserRecord);
//            log.info("保存推送失败记录日志到 TB_DB_PURCHASER_RECORD中成功!!!");
//        }
//    }
//
//
//    /**
//     * 构建厂商推送数据记录
//     *
//     * @param dbId        当前推送的专科库id
//     * @param purchaserId 厂商id
//     * @param pushStatus  推送数据状态 0-成功 1-失败
//     * @param total
//     * @param consume
//     * @return
//     */
//    private TbDbPurchaserRecord getTbDbPurchaserRecord(String dbId, String purchaserId, String pushStatus, long total, long consume) {
//        TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
//        purchaserRecord.setBakId("");
//        purchaserRecord.setExternalDbId(dbId);
//        purchaserRecord.setConsume(String.valueOf(consume));
//        purchaserRecord.setPurchaserId(purchaserId);
//        purchaserRecord.setStatus(pushStatus);
//        purchaserRecord.setTotal(String.valueOf(total));
//        return purchaserRecord;
//    }
//
//    protected boolean existRecord(String dbId) {
//        return CollectionUtils.isNotEmpty(getPushPurchasersRecordList(dbId));
//    }
//
//    private List<TbDbPurchaserRecord> getPushPurchasersRecordList(String dbId) {
//        List<TbDbPurchaserRecord> tbDbPurchaserRecords = null;
//        try {
//            EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
//            wrapper.eq("EXTERNAL_DB_ID", dbId);
//            tbDbPurchaserRecords = purchaserRecordService.selectList(wrapper);
//        } catch (Exception e) {
//            log.error("查询专科库是否推送过失败!!! 数据库dbId : {},{}", dbId);
//        }
//        return tbDbPurchaserRecords;
//    }
//
//    /**
//     * 获取所有可用的数据库ids
//     *
//     * @param dbId 数据库id
//     * @return 所有可用的数据库id
//     */
//    protected List<String> getAvailableDataSource(String dbId) {
//        List<String> dbIds = new ArrayList<>();
//        if (StringUtils.isEmpty(dbId)) {
//            dbIds = tbDbResourceService.selectDbIds();
//        } else {
//            // 判断该数据库是否可用或者是全院库
//            TbDbResource tbDbResource = tbDbResourceService.selectById(dbId);
//            if (tbDbResource == null || !DATASOURCE_ENABLED_STATUS.equalsIgnoreCase(tbDbResource.getStatus())
//                    || QUAN_YUAN_KU_STATUS.equalsIgnoreCase(tbDbResource.getHospitalFlag())) {
//                log.info("数据库不可用或是全院库!!! 数据库id：{}", dbId);
//            } else {
//                dbIds.add(dbId);
//            }
//        }
//        log.info("所有可用数据库ids：{}", dbIds);
//        return dbIds;
//    }
//
//
//    /**
//     * 获取需要推送的厂商地址
//     *
//     * @param dbId
//     * @return
//     */
//    private Map<String, String> getPushPurchasersMap(String dbId) {
//        Map<String, String> purchasersMap = getPurchasersMap();
//
//        List<TbDbPurchaserRecord> recordList = getPushPurchasersRecordList(dbId);
//        if (CollectionUtils.isNotEmpty(recordList)) {
//            Map<String, String> map = new HashMap<>(recordList.size());
//            for (TbDbPurchaserRecord record : recordList) {
//                String purchaserId = record.getPurchaserId();
//                String status = record.getStatus();
//                String url = purchasersMap.get(purchaserId);
//                if (!purchasersMap.containsKey(purchaserId) && PushStatusConstant.SUCCESS.equalsIgnoreCase(status)) {
//                    map.put(purchaserId, url);
//                }
//            }
//            purchasersMap = map;
//        }
//        log.info("当前需要推送厂商列表:{}, 数据库dbId: {}", purchasersMap, dbId);
//        return purchasersMap;
//    }
//
//    /**
//     * 获取所有可用的厂商
//     *
//     * @return 可用的厂商名称和调用地址
//     */
//    private Map<String, String> getPurchasersMap() {
//        EntityWrapper<TbDbPurchaser> purchaserEntityWrapper = new EntityWrapper<>();
//        purchaserEntityWrapper.eq("STATUS", PurchaserStatusConstant.ENABLED);
//        // 厂商推送地址不能为空
//        purchaserEntityWrapper.isNotNull("CUSTOM0");
//        purchaserEntityWrapper.setSqlSelect(" id ", " CUSTOM0 ");
//        List<TbDbPurchaser> tbDbPurchasers = purchaserService.selectList(purchaserEntityWrapper);
//        Map<String, String> purchasersMap = new HashMap<>(tbDbPurchasers.size());
//        for (TbDbPurchaser tbDbPurchaser : tbDbPurchasers) {
//            String id = tbDbPurchaser.getId();
//            String url = tbDbPurchaser.getCustom0();
//            if (!purchasersMap.containsKey(id)) {
//                purchasersMap.put(id, url);
//            }
//        }
//        log.info("当前可用厂商列表:{}", purchasersMap);
//        return purchasersMap;
//    }
//
//
//    protected SearchRequest getSearchRequest(String dbId) {
//        SearchRequest searchRequest = null;
//        EntityWrapper<TbPatientuniqueidBackups> wrapper = new EntityWrapper<>();
////        wrapper.eq("DB_ID", dbId);
////        wrapper.eq("STATUS", PushStatusConstant.PUSHED);
////        List<TbPatientuniqueidBackups> backups1 = patientuniqueidBackupsService.selectList(wrapper);
//
//        wrapper.eq("DB_ID", dbId);
//        wrapper.eq("STATUS", PushStatusConstant.NOT_PUSHED).or().isNull("STATUS");
//        List<TbPatientuniqueidBackups> backups2 = patientuniqueidBackupsService.selectList(wrapper);
////        backups2.removeAll(backups1);
//
//        if (CollectionUtils.isNotEmpty(backups2)) {
//            List<SearchRequest> searchRequests = new LinkedList<>();
//            Set<String> pIdSet = new HashSet<>(backups2.size());
//            for (TbPatientuniqueidBackups backups : backups2) {
//                String patientUniqueId = backups.getPatientUniqueId();
//                if (!pIdSet.contains(patientUniqueId)) {
//                    TermSearchRequest termSearchRequest = new TermSearchRequest();
//                    termSearchRequest.setField("patient_unique_id.keyword");
//                    termSearchRequest.setValue(patientUniqueId);
//                    searchRequests.add(new ShouldSearchRequest(termSearchRequest));
//                    pIdSet.add(patientUniqueId);
//                }
//            }
//            searchRequest = new BoolSearchRequest(searchRequests);
//        }
//        return searchRequest;
//    }
//
//
//    // 暂定厂商返回实体类
//    @Data
//    private class ResultEntity {
//        private String msg;
//        private Integer status = 200;
//    }
//
//}
