package com.djhu.service.push;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.common.constant.PurchaserStatusConstant;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.common.util.URLUtils;
import com.djhu.elasticsearch.core.ElasticsearchTemplate;
import com.djhu.elasticsearch.core.handle.MsgHandle;
import com.djhu.elasticsearch.core.request.MacthAllRequest;
import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.entity.HisInfoEntity;
import com.djhu.entity.MsgInfo;
import com.djhu.entity.ResultEntity;
import com.djhu.entity.atses.TbDbPurchaser;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.service.ITbDbPurchaserRecordService;
import com.djhu.service.ITbDbPurchaserService;
import com.djhu.service.ITbDbResourceService;
import com.djhu.service.ITbPatientuniqueidBackupsService;
import com.djhu.service.query.IQueryDataService;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 17:15
 **/
@Slf4j
@Service
public class QueryAndPushServiceImpl implements IQueryAndPushService {

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

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Value("${djhu.push.batchNumber:1000}")
    private Integer batchNumber;

    private volatile boolean isRunning = false;


    @Override
    public void dispose(String dbId, MsgInfo msgInfo, Integer pushType) {
        if (isRunning == true) {
            log.info("程序正在运行!!! 等待中... pushType: {}", pushType);
            return;
        }
        try {
            List<String> dbIds = getAvailableDataSource(dbId);
            for (String resourceDbId : dbIds) {

                // 取所有厂商
                Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId, pushType);
                for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
                    String purchasersId = entry.getKey();
                    String purchasersUrl = entry.getValue();

                    if (!URLUtils.isConnection(purchasersUrl)) {
                        log.info("厂商连接不可用!!! 厂商ID: {}, 厂商URL: {}", purchasersId, purchasersUrl);
                        continue;
                    }

                    SearchRequest searchRequest = null;
                    if (ALL.equals(pushType) && isFirst(purchasersId, resourceDbId)) {
                        searchRequest = new MacthAllRequest();
                    } else if (CREATE_OR_UPDATE.equals(pushType)) {
                        searchRequest = new MacthAllRequest();
                    } else {
                        Object obj = queryDataService.findById(msgInfo.getIndex(), msgInfo.getType(), msgInfo.getId());
                        if (obj == null) {
                            return;
                        }
                        Map<String, Object> map = (Map<String, Object>) obj;
                        boolean exist = purchaserRecordService.exist(resourceDbId, purchasersId, map);
                        if (exist) {
                            log.info("推送过，不处理!!!");
                        } else {
                            pushDataAndUpdateStatus(purchasersId, purchasersUrl, Arrays.asList(map), resourceDbId);
                        }
                    }

                    String esSuffix = tbDbResourceService.getEsSuffixByDbId(resourceDbId);
                    if (searchRequest == null || StringUtils.isEmpty(esSuffix)) {
                        return;
                    }
                    ((MacthAllRequest) searchRequest).setIndex(GlobalConstant.HIUP_PERSON_INDEX + "_" + esSuffix);
                    ((MacthAllRequest) searchRequest).setType(GlobalConstant.HIUP_PERSON_TYPE);
                    long total = 0;
                    try {
                        total = elasticsearchTemplate.count(searchRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (total > 0) {
                        Set<String> patientKeySet = getPatientKeySet(resourceDbId, purchasersId);

                        List<Map<String, Object>> mapList = new ArrayList<>();
                        long startTime = System.currentTimeMillis();
                        try {
                            elasticsearchTemplate.queryForHandle(searchRequest, new MsgHandle() {
                                @Override
                                public void handle(Object o) {
                                    Map<String, Object> map = (Map<String, Object>) o;
                                    HisInfoEntity hisInfoEntity = new HisInfoEntity(map).invoke();
                                    String hisId = hisInfoEntity.getHisId();
                                    String hisDomainId = hisInfoEntity.getHisDomainId();
                                    String hisVisitId = hisInfoEntity.getHisVisitId();
                                    String hisVisitDomainId = hisInfoEntity.getHisVisitDomainId();

                                    String patientKey = getPatientKey(hisId, hisDomainId, hisVisitId, hisVisitDomainId);
                                    if (!patientKeySet.contains(patientKey)) {

                                        int index = atomicInteger.incrementAndGet();
                                        mapList.add(map);
                                        if (index % batchNumber == 0) {
                                            pushDataAndUpdateStatus(purchasersId, purchasersUrl, mapList, resourceDbId);
                                            mapList.clear();
                                        }
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (CollectionUtils.isNotEmpty(mapList)) {
                            pushDataAndUpdateStatus(purchasersId, purchasersUrl, mapList, resourceDbId);
                            mapList.clear();
                        }
                        long consume = System.currentTimeMillis() - startTime;
                        log.info("推送厂商数据成功!!! 推送数据量为：{} 条,耗时：{} ms", total, consume);
                    } else {
                        log.info("需要推送的数据为空,不用处理!!!");
                    }
                }
            }
        } finally {
            isRunning = false;
        }
    }

    @Override
    public boolean running() {
        return isRunning;
    }


    private Set<String> getPatientKeySet(String resourceDbId, String purchasersId) {
        EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("DB_ID", resourceDbId);
        wrapper.eq("PURCHASER_ID", purchasersId);
        wrapper.eq("STATUS", PushStatusConstant.SUCCESS);
        List<TbDbPurchaserRecord> records = purchaserRecordService.selectList(wrapper);
        Set<String> filterSet = new HashSet<>(records.size());
        for (TbDbPurchaserRecord record : records) {
            String patientKey = getPatientKey(record.getHisId(), record.getHisDomainId(), record.getHisVisitId(), record.getHisVisitDomainId());
            if (!filterSet.contains(patientKey) && StringUtils.isNotEmpty(patientKey)) {
                filterSet.add(patientKey);
            }
        }
        return filterSet;
    }

    private String getPatientKey(String hisId, String hisDomainId, String hisVisitId, String hisVisitDomainId) {
        return Joiner.on("_").useForNull("").join(hisId, hisDomainId, hisVisitId, hisVisitDomainId);
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
    public List<String> getAvailableDataSource(String dbId) {
        List<String> dbIds = tbDbResourceService.selectDbIds(dbId);
        log.info("所有可用数据库ids：{}", dbIds);
        return dbIds;
    }


    private void pushDataAndUpdateStatus(String purchasersId, String purchasersUrl, List<Map<String, Object>> mapList, String resourceDbId) {
        long startTime = System.currentTimeMillis();
        try {
//            log.info("开始调用厂商接口执行推送, 厂商id：{}，厂商地址：{}", purchasersId, purchasersUrl);
//            log.info("推送患者编号patientUniqueId: {}",patientUniqueId);

            JSONObject object = new JSONObject();
            object.put("db_id", resourceDbId);
            object.put("data", mapList);
//            Object o = JSON.toJSON();
//            JSONObject json = JSON.parseObject(o.toString());

            ResponseEntity<ResultEntity> response = restTemplate.postForEntity(purchasersUrl, object, ResultEntity.class);
            // 先设定一个模拟返回实体
            ResultEntity result = response.getBody();
            long consume = System.currentTimeMillis() - startTime;
            log.info("返回结果为：{} 耗时: {} ms", result, consume);

            for (Map<String, Object> objectMap : mapList) {
                insertRecord(purchasersId, resourceDbId, result, objectMap);
            }
        } catch (Exception e) {
            log.error("推送厂商数据失败!!! 厂商id：{}, 厂商url: {}", purchasersId, purchasersUrl);
            long consume = System.currentTimeMillis() - startTime;
            log.info("推送厂商数据失败!!! 耗时: {} ms,{}", consume, e);
            return;
        }
    }

    private void insertRecord(String purchasersId, String resourceDbId, ResultEntity result, Map<String, Object> objectMap) {
        HisInfoEntity hisInfoEntity = new HisInfoEntity(objectMap).invoke();
        String hisId = hisInfoEntity.getHisId();
        String hisDomainId = hisInfoEntity.getHisDomainId();
        String hisVisitId = hisInfoEntity.getHisVisitId();
        String hisVisitDomainId = hisInfoEntity.getHisVisitDomainId();

        TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
        purchaserRecord.setId(IdWorker.getIdStr());
        purchaserRecord.setPurchaserId(purchasersId);
        purchaserRecord.setHisId(hisId);
        purchaserRecord.setHisVisitId(hisVisitId);
        purchaserRecord.setHisDomainId(hisDomainId);
        purchaserRecord.setHisVisitDomainId(hisVisitDomainId);
        purchaserRecord.setDbId(resourceDbId);
        if (result.getStatus() == 200) {
            purchaserRecord.setStatus(PushStatusConstant.SUCCESS);
        } else {
            purchaserRecord.setStatus(PushStatusConstant.ERROR);
        }
        try {
            purchaserRecordService.insert(purchaserRecord);
        } catch (Exception e) {
            log.error("保存记录失败!!! {}", e);
            return;
        }
    }

    /**
     * 获取需要推送的厂商地址
     *
     * @param dbId
     * @param pushType
     * @return
     */
    private Map<String, String> getPushPurchasersMap(String dbId, Integer pushType) {
        Map<String, String> purchasersMap = getPurchasersMap();
        if (ALL.equals(pushType)) {
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
        }
        log.info("当前需要推送厂商列表:{}, 数据库dbId: {}", purchasersMap, dbId);
        return purchasersMap;
    }

    private List<TbDbPurchaserRecord> getPushPurchasersRecordList(String dbId) {
        List<TbDbPurchaserRecord> tbDbPurchaserRecords = null;
        try {
            EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
            wrapper.eq("DB_ID", dbId);
            wrapper.setSqlSelect("distinct(PURCHASER_ID)", "status");
            tbDbPurchaserRecords = purchaserRecordService.selectList(wrapper);
        } catch (Exception e) {
            log.error("查询专科库是否推送过失败!!! 数据库dbId : {},{}", dbId, e);
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
