//package com.djhu.service.push;
//
//import com.alibaba.dubbo.common.utils.CollectionUtils;
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
//import com.djhu.service.ITbDbPurchaserRecordService;
//import com.djhu.service.ITbDbPurchaserService;
//import com.djhu.service.ITbDbResourceService;
//import com.djhu.service.ITbPatientuniqueidBackupsService;
//import com.djhu.service.query.IQueryDataService;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
///**
// * @author cyf
// * @description
// * @create 2020-04-24 18:16
// **/
//@Slf4j
////@Service
//@Deprecated
//public class IPushDataServiceImpl implements IPushDataService {
//
//    /**
//     * 数据库的可用状态 2-为已完成创建的数据库
//     */
//    private static final String DATASOURCE_ENABLED_STATUS = "2";
//    /**
//     * 全院库的状态，推送数据不推送全院库数据
//     */
//    private static final String QUAN_YUAN_KU_STATUS = "1";
//
//    @Autowired
//    private ITbDbResourceService tbDbResourceService;
//    @Autowired
//    private ITbDbPurchaserService purchaserService;
//    @Autowired
//    private ITbDbPurchaserRecordService purchaserRecordService;
//    @Autowired
//    private ITbPatientuniqueidBackupsService backupsService;
//    @Autowired
//    private RestTemplate restTemplate;
//    @Autowired
//    IQueryDataService queryDataService;
//
//
//    @Override
//    public void push(Integer pushType) {
//        push(null, null,pushType);
//    }
//
//    @Override
//    public void push(String dbId,Integer pushType) {
//        push(dbId, null,pushType);
//    }
//
//
//    @Override
//    public void push(String dbId, MsgInfo msgInfo,Integer pushType) {
//        //1. 查询所有可用的数据库（除了全院库）
//        List<String> dbIds = getAvailableDataSource(dbId);
//        if (CollectionUtils.isEmpty(dbIds)) {
//            log.info("可用数据库列表为空!!!");
//            return;
//        }
//
//        // 2. 查询所有可用的厂商
//        Map<String, String> purchasersMap = getPurchasersMap();
//
//        for (String resourceDbId : dbIds) {
//            // 3. 判断当前库是否推送过对应的厂商数据
//            EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
//            wrapper.eq("EXTERNAL_DB_ID", resourceDbId);
//            List<TbDbPurchaserRecord> tbDbPurchaserRecords = purchaserRecordService.selectList(wrapper);
//
//            // 数据库下所有厂商都是首次推送
//            if(CollectionUtils.isEmpty(tbDbPurchaserRecords)){
//                for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
//                    String purchaserId = entry.getKey();
//                    String url = entry.getValue();
//                    pushAndAfterDispose(resourceDbId,url,purchaserId,true);
//                }
//            }else{
//                // 查找数据库下没有推送过的和推送失败的厂商
//                for (TbDbPurchaserRecord record : tbDbPurchaserRecords) {
//                    String purchaserId = record.getPurchaserId();
//                    String status = record.getStatus();
//                    String url = purchasersMap.get(purchaserId);
//
//                    if (purchasersMap.containsKey(purchaserId) && PushStatusConstant.SUCCESS.equalsIgnoreCase(status)) {
//                        // 推送过，暂不处理
//                        pushAndAfterDispose(resourceDbId,url,purchaserId,false);
//                        continue;
//                    } else {
//                        // 推送并执行后续处理
//                        pushAndAfterDispose(resourceDbId, url, purchaserId,true);
//                    }
//                }
//            }
//        }
//
//        if (StringUtils.isEmpty(dbId)) {
//            // 推送所有数据库（除了全院库）数据
//
//            /*
//            1. 查询所有可用的数据库（除了全院库）
//            2. 查询所有厂商
//            3. 判断是否推送过对应的厂商数据
//                    是 - 暂不处理
//                    否-重新推送。更新推送失败原因
//             */
//        }
//
//        if (StringUtils.isNotEmpty(dbId) && msgInfo == null) {
//            // 推送指定数据库数据
//
//            /*
//            1. 查询所有厂商
//            2. 判断是否推送过对应的厂商数据
//                    - 是   需要执行更新
//                            用状态为 2 的数据查询状态为1 的数据，查询结果进行处理
//                                - 简单点，只做新增（用状态2集合remove状态1数据），得出的结果直接记录为新增
//
////                                - 查询到的为    为更新状态
////                                - 查询不到的为  为新增状态
////                                - 如果
//                    - 否   新建库，直接推送
//                            for 所有厂商请求地址
//                                - 1. 调用需要推送的厂商地址，执行推送。
//                                - 2. 更新推送记录 TB_DB_PURCHASER_RECORD
//                                - 3. 先删除 当前库 TbPatientuniqueidBackups 表中状态为 1 的数据
//                                - 4. 再改 TbPatientuniqueidBackups 中数据状态为 1 （1-已推送 2-未推送）
//             */
//        }
//
//        if (StringUtils.isNotEmpty(dbId) && msgInfo != null) {
//            // 推送单条数据
//
//            /*
//            1.  根据msgInfo 查询oarcle记录表 p_id_bak 是否推送过。
//                        是 - 更改为更新状态 直接推送
//                        否 - 更改为新增状态 直接推送
//             */
//        }
//
//    }
//
//    // todo 需要添加事务
//    private void pushAndAfterDispose(String resourceDbId, String url, String purchaserId,boolean isFirst) {
//        // todo 考虑每次推送多少条数据  -- 加入专科库有10万条，用递归调用推送可以吗。
//        List<Map<String, Object>> pushDataList = getPushDataList(resourceDbId,isFirst);
//        // 调用接口 执行推送
//        ResponseEntity<ResultEntity> response = restTemplate.postForEntity(url, pushDataList, ResultEntity.class);
//        // 先设定一个模拟返回实体
//        ResultEntity result = response.getBody();
//        log.info("厂商id: {} 推送数据量为：{} 条", purchaserId, pushDataList.size());
//        log.info("返回结果为：{}", result);
//
//        // 成功后
//        if (result.getStatus() == 200) {
//            /*
//            1. 删除 bak 表中状态为1的数据
//            2. 设置 bak 表中状态为2的数据改为1
//            3. 记录 record 厂商推送记录到表
//             */
//            EntityWrapper<TbPatientuniqueidBackups> backupsEntityWrapper = new EntityWrapper<>();
//            backupsEntityWrapper.eq("DB_ID", resourceDbId);
//            backupsEntityWrapper.eq("STATUS", PushStatusConstant.PUSHED);
//            backupsService.delete(backupsEntityWrapper);
//
//            backupsEntityWrapper.eq("STATUS", PushStatusConstant.NOT_PUSHED);
//            List<TbPatientuniqueidBackups> backupsList = backupsService.selectList(backupsEntityWrapper);
//            backupsService.insertOrUpdateBatch(backupsList);
//
//            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(resourceDbId, purchaserId, PushStatusConstant.SUCCESS);
//            purchaserRecordService.insert(purchaserRecord);
//        } else {
//            // 记录 record 厂商推送失败状态，执行重试
//            TbDbPurchaserRecord purchaserRecord = getTbDbPurchaserRecord(resourceDbId, purchaserId, PushStatusConstant.ERROR);
//            purchaserRecordService.insert(purchaserRecord);
//        }
//    }
//
//    /**
//     *  获取所有可用的数据库ids
//     * @param dbId  数据库id
//     * @return      所有可用的数据库id
//     */
//    private List<String> getAvailableDataSource(String dbId) {
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
//        return dbIds;
//    }
//
//    /**
//     * 获取所有可用的厂商
//     * @return  可用的厂商名称和调用地址
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
//        return purchasersMap;
//    }
//
//    /**
//     *  构建厂商推送数据记录
//     * @param dbId          当前推送的专科库id
//     * @param purchaserId   厂商id
//     * @param pushStatus    推送数据状态 0-成功 1-失败
//     * @return
//     */
//    private TbDbPurchaserRecord getTbDbPurchaserRecord(String dbId, String purchaserId, String pushStatus) {
//        // todo 这里的参数需要补充完整
//        TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
//        purchaserRecord.setBakId(dbId);
//        purchaserRecord.setExternalDbId(dbId);
//        purchaserRecord.setConsume(null);
//        purchaserRecord.setPurchaserId(purchaserId);
//        purchaserRecord.setStatus(pushStatus);
//        purchaserRecord.setEsIndex("");
//        purchaserRecord.setEsType("");
//        purchaserRecord.setTotal("");
//        return purchaserRecord;
//    }
//
//    /**
//     *  获取需要推送的数据，TODO 这个考虑一下查询多少数据进行推送
//     * @param dbId      当前专科库id
//     * @param isFirst   是否首次推送
//     * @return          需要推送的数据
//     */
//    private List<Map<String, Object>> getPushDataList(String dbId,boolean isFirst) {
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        /*
//         查询 bak 状态为 1的数据作为a1
//         查询 bak 状态为 2的数据作为a2
//         用a2 删除 a1 数据 == 需要推送的数据
//         */
//        if(isFirst){
//            resultList = queryDataService.findAll(dbId);
//        }else{
//            EntityWrapper<TbPatientuniqueidBackups> wrapper = new EntityWrapper<>();
//            wrapper.eq("DB_ID",dbId);
//            wrapper.eq("STATUS",PushStatusConstant.PUSHED);
//            List<TbPatientuniqueidBackups> backups1 = backupsService.selectList(wrapper);
//
//            wrapper.eq("STATUS",PushStatusConstant.NOT_PUSHED);
//            List<TbPatientuniqueidBackups> backups2 = backupsService.selectList(wrapper);
//            backups2.removeAll(backups1);
//
//            if(CollectionUtils.isNotEmpty(backups2)){
//                List<SearchRequest> searchRequests = new LinkedList<>();
//                Set<String> pIdSet = new HashSet<>(backups2.size());
//                for (TbPatientuniqueidBackups backups : backups2) {
//                    String patientUniqueId = backups.getPatientUniqueId();
//                    if(!pIdSet.contains(patientUniqueId)){
//                        TermSearchRequest termSearchRequest = new TermSearchRequest();
//                        termSearchRequest.setField("patient_unique_id.keyword");
//                        termSearchRequest.setValue(patientUniqueId);
//                        searchRequests.add(new ShouldSearchRequest(termSearchRequest));
//                        pIdSet.add(patientUniqueId);
//                    }
//                }
//                BoolSearchRequest searchRequest = new BoolSearchRequest(searchRequests);
//                resultList = queryDataService.findAll(dbId, searchRequest);
//            }
//        }
//        return resultList;
//    }
//
//    // 暂定厂商返回实体类
//    @Data
//    private class ResultEntity {
//        private String msg;
//        private Integer status = 200;
//    }
//}
