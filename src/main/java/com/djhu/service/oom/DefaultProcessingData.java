package com.djhu.service.oom;

import com.djhu.common.constant.DataConst;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.page.Pagination;
import com.djhu.elasticsearch.core.request.MacthAllRequest;
import com.djhu.entity.CreateDataInfo;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.repository.ForeignDocumentCallbackHandler;
import com.djhu.service.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
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
            dbId = tbDbResourceService.getDbIdByIndex(index);

        }
        List<String> dbIds = getAvailableDataSource(dbId);
        for (String resourceDbId : dbIds) {

            Map<String, String> purchasersMap = getPushPurchasersMap(resourceDbId, handleType);
            for (Map.Entry<String, String> entry : purchasersMap.entrySet()) {
                String purchasersId = entry.getKey();
                String purchasersUrl = entry.getValue();

                List list;
                if (DataConst.ALL.equals(handleType) || DataConst.CREATE_OR_UPDATE.equals(handleType)) {
                    if(createDataInfo ==  null || StringUtils.isEmpty(createDataInfo.getCollection())){
                        queryEsAndPush(resourceDbId,purchasersId,purchasersUrl,filter);
                    }else {
                        queryMongoAndPush(createDataInfo, resourceDbId, purchasersId, purchasersUrl,filter);
                    }
                } else if (DataConst.INCREMENT.equals(handleType)) {
                    Object obj = prepareData.getBy(id, index, type, purchasersId, filter);
                    if (obj != null) {
                        list = Arrays.asList(obj);
                        sendData.send(list,purchasersUrl,resourceDbId,purchasersId);
                    }else {
                        log.info("数据为空或者已推送过，不处理!!!");
                        continue;
                    }
                } else {
                    log.info("暂无此类型!!!");
                    continue;
                }
            }
        }
    }

    private void queryMongoAndPush(CreateDataInfo createDataInfo, String resourceDbId, String purchasersId, String purchasersUrl, Filter filter) {
        MongoTemplate mongoTemplate = mongoTemplateFactory.getMongoTemplate(createDataInfo);
        long total = mongoTemplate.count(new Query(), createDataInfo.getCollection());
        if(total == 0){
            queryEsAndPush(resourceDbId,purchasersId,purchasersUrl,filter);
        }else {
            Query query = new Query();
            Meta meta = new Meta();
            meta.addFlag(Meta.CursorOption.NO_TIMEOUT);
            query.setMeta(meta);
            ForeignDocumentCallbackHandler callbackHandler = new ForeignDocumentCallbackHandler();
            callbackHandler.setFilter(filter);
            callbackHandler.setDbId(resourceDbId);
            callbackHandler.setUrl(purchasersUrl);
            callbackHandler.setSendData(sendData);
            callbackHandler.setPurchaserRecordService(purchaserRecordService);
            applicationContext.getAutowireCapableBeanFactory().autowireBean(callbackHandler);
            mongoTemplate.executeQuery(query,createDataInfo.getCollection(),callbackHandler);
        }
    }

    private void queryEsAndPush(String resourceDbId, String purchasersId, String purchasersUrl, Filter filter) {
        MacthAllRequest searchRequest = new MacthAllRequest();
        String esSuffix = tbDbResourceService.getEsSuffixByDbId(resourceDbId);
        if (StringUtils.isEmpty(esSuffix)) {
            log.warn("数据库不合法,esSuffix 为空!!! dbId: {}",resourceDbId);
            return;
        }
        searchRequest.setIndex(GlobalConstant.HIUP_PERSON_INDEX + "_" + esSuffix);
        searchRequest.setType(GlobalConstant.HIUP_PERSON_TYPE);
        long total = 0;
        try {
            total = elasticsearchTemplate.count(searchRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (total > 0) {
            try {
                Pagination pagination = new Pagination();
                pagination.setPageNo(1);
                pagination.setPageSize(10);
                Pagination page = elasticsearchTemplate.queryforPage(pagination, searchRequest);
                List<Map<String, Object>> datas = page.getDatas();
                for (Map<String, Object> data : datas) {
                    if(!filter.accept(resourceDbId,purchasersId,data)){
                        sendData.send(Arrays.asList(data),purchasersUrl,resourceDbId,purchasersId);
                    }else {
                        log.info("数据已存在!!!");
                        continue;
                    }
                }
//                elasticsearchTemplate.queryForHandle(searchRequest, o -> {
//                     sendData.send(datas,purchasersUrl,resourceDbId,purchasersId);
//                });
            } catch (Exception e) {
                log.warn("查询失败！！！ dbId: {},{}",resourceDbId,e.getMessage());
            }
        } else {
            log.info("需要推送的数据为空,不用处理!!!");
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
}
