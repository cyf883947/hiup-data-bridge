package com.djhu.service.oom;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.common.constant.DataConst;
import com.djhu.common.constant.PurchaserStatusConstant;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.entity.CreateDataInfo;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.entity.atses.TbDbPurchaser;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.repository.MongoTemplateFactory;
import com.djhu.service.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 10:18
 **/
@Data
@Slf4j
public abstract class AbstractProcessingData implements ProcessingData,ApplicationContextAware, InitializingBean {

    protected ITbDbResourceService tbDbResourceService;
    protected ITbDbPurchaserRecordService purchaserRecordService;
    private ITbDbPurchaserService purchaserService;
    protected IPrepareData prepareData;
    protected ISendData sendData;
    protected MongoTemplateFactory mongoTemplateFactory;
    protected Filter filter;

    private ApplicationContext applicationContext;


    // 针对全部数据库（除全院库和未完成创建的）
    @Override
    public void handle() {
        handle(null,DataConst.ALL);
    }

    // 针对指定专科库的创建或更新数据库范围
    @Override
    public void handle(String dbId, Integer handleType) {
        handle(dbId,null,null,null,null,handleType,null);
    }

    @Override
    public void handle(String dbId, Integer handleType, CreateDataInfo createDataInfo) {
        handle(dbId,null,null,null,null,handleType,createDataInfo);
    }

    // 针对数据库增量
    @Override
    public void handle(String id, String index, String type, Integer handleType) {
        handle(null,id,index,type,null,handleType,null);
    }

    protected abstract void handle(String dbId, String id, String index, String type, HIsInfoRequest hIsInfoRequest, Integer handleType, CreateDataInfo createDataInfo);

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
            if (tbDbResource == null || !DataConst.DATASOURCE_ENABLED_STATUS.equalsIgnoreCase(tbDbResource.getStatus())
                    || DataConst.QUAN_YUAN_KU_STATUS.equalsIgnoreCase(tbDbResource.getHospitalFlag())) {
                log.info("数据库不可用或是全院库!!! 数据库id：{}", dbId);
            } else {
                dbIds.add(dbId);
            }
        }
        log.info("所有可用数据库ids：{}", dbIds);
        return dbIds;
    }

    /**
     * 获取需要推送的厂商地址
     *
     * @param dbId
     * @param pushType
     * @return
     */
    protected Map<String, String> getPushPurchasersMap(String dbId, Integer pushType) {
        Map<String, String> purchasersMap = getPurchasersMap();
        if (DataConst.ALL.equals(pushType)) {
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


    @Override
    public void afterPropertiesSet() throws Exception {
        this.tbDbResourceService = applicationContext.getBean(ITbDbResourceService.class);
        this.purchaserRecordService = applicationContext.getBean(ITbDbPurchaserRecordService.class);
        this.purchaserService = applicationContext.getBean(ITbDbPurchaserService.class);
        this.prepareData = applicationContext.getBean(IPrepareData.class);
        this.sendData = applicationContext.getBean(ISendData.class);
        this.filter = applicationContext.getBean(Filter.class);
        this.mongoTemplateFactory = applicationContext.getBean(MongoTemplateFactory.class);
    }

    @PostConstruct
    public void method(){
        System.out.println("method.....");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("setApplicationContext.....");
    }
}
