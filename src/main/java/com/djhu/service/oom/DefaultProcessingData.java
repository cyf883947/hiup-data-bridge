package com.djhu.service.oom;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.common.constant.DataConst;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.entity.CreateDataInfo;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.repository.ForeignDocumentCallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Meta;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 10:20
 **/
@Slf4j
@Service
public class DefaultProcessingData extends AbstractProcessingData {

    @Override
    protected void handle(String dbId, String id, String index, String type, HIsInfoRequest hIsInfoRequest
            , Integer handleType , CreateDataInfo createDataInfo) {
        validate(dbId, index, handleType);
        if (StringUtils.isEmpty(type)) {
            type = GlobalConstant.HIUP_PERSON_TYPE;
        }
        if (StringUtils.isNotEmpty(index)) {
            dbId = getDbIdByIndex(index);
        }
        List<String> dbIds = getAvailableDataSource(dbId);
        for (String resourceDbId : dbIds) {

            Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId, handleType);
            for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
                String purchasersId = entry.getKey();
                String purchasersUrl = entry.getValue();

                List list = null;
                if (DataConst.ALL.equals(handleType) || DataConst.CREATE_OR_UPDATE.equals(handleType)) {
//                    list = prepareData.list(resourceDbId, purchasersId, filter);
                    if(createDataInfo ==  null || StringUtils.isEmpty(createDataInfo.getCollection())){
                        // 从es 中查询数据
                    }else {
                        // mongo 里没有，就去查es
                        Query query = new Query();
                        Meta meta = new Meta();
                        meta.addFlag(Meta.CursorOption.NO_TIMEOUT);
                        query.setMeta(meta);
                        ForeignDocumentCallbackHandler callbackHandler = new ForeignDocumentCallbackHandler();
                        callbackHandler.setUrl(purchasersUrl);
                        callbackHandler.setSendData(sendData);
                        callbackHandler.setPurchaserRecordService(purchaserRecordService);
                        super.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(callbackHandler);
                        mongoTemplateFactory.getMongoTemplate(createDataInfo).executeQuery(query,createDataInfo.getCollection(),callbackHandler);
                    }
                } else if (DataConst.INCREMENT.equals(handleType)) {
                    Object obj = prepareData.getBy(id, index, type, purchasersId, filter);
                    if (obj != null) {
                        list = Arrays.asList(obj);
                        sendData.send(list, purchasersUrl);
                    }
                } else {
                    log.info("暂无此类型!!!");
                    continue;
                }
            }
        }


    }

    private void validate(String dbId, String index, Integer handleType) {
        if (DataConst.ALL.equals(handleType)) {
            // 全部数据库，参数都可以为空
        } else if (DataConst.CREATE_OR_UPDATE.equals(handleType)) {
            // 创建或更新数据库，dbId 不能为空
            if (StringUtils.isEmpty(dbId)) {
                throw new RuntimeException("创建或更新数据库数据库范围时，数据库 dbId 不能为空!!!");
            }
        } else if (DataConst.INCREMENT.equals(handleType)) {
            // 增量, 参数index 不能为空，id不能为空
            if (StringUtils.isEmpty(index)) {
                throw new RuntimeException("数据库增量，es索引 index 不能为空!!!");
            }
        } else {
            throw new RuntimeException("暂不支持其他同步数据类型!!!");
        }
    }

    private static final String PREFIX = "person_";

    private String getDbIdByIndex(String index) {
        String esSufffix = index.substring(index.indexOf(PREFIX) + PREFIX.length());
        EntityWrapper<TbDbResource> wrapper = new EntityWrapper<>();
        wrapper.eq("ES_SUFFIX", esSufffix);
        TbDbResource tbDbResource = tbDbResourceService.selectOne(wrapper);
        if (tbDbResource != null) {
            String resourceDbId = tbDbResource.getDbId();
            return resourceDbId;
        }
        return null;
    }
}
