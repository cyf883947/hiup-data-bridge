package com.djhu.service.oom;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.ElasticsearchTemplate;
import com.djhu.elasticsearch.core.RequestUtils;
import com.djhu.elasticsearch.core.request.AbstractRequest;
import com.djhu.elasticsearch.core.request.DefaultGetDelRequest;
import com.djhu.elasticsearch.core.request.MacthAllRequest;
import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.service.Filter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 18:43
 **/
@Slf4j
@Data
@Service
public class DefaultPrepareData extends AbstractPrepareData {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    protected List list(String dbId, SearchRequest searchRequest, String purchaserId, Filter filter) {
        String index = getIndex(dbId);
        if (StringUtils.isEmpty(index)) {
            throw new RuntimeException("index 不能为空!!!");
        }
        String type = GlobalConstant.HIUP_PERSON_TYPE;
        AbstractRequest abstractRequest;
        if (searchRequest == null) {
            abstractRequest = new MacthAllRequest();
        } else {
            abstractRequest = (AbstractRequest) searchRequest;
        }
        abstractRequest.setIndex(index);
        abstractRequest.setType(type);
        abstractRequest.setTimeout(GlobalConstant.DEFAUT_TIME_OUT);
        searchRequest = (SearchRequest) abstractRequest;

        try {
            long startTime = System.currentTimeMillis();
            log.info(" 查询 es 数据请求为 {}", RequestUtils.request2josn(searchRequest));
            List<Map<String, Object>> mapList = elasticsearchTemplate.queryforList(searchRequest);
            int size = 0;
            if (CollectionUtils.isNotEmpty(mapList)) {
                size = mapList.size();
                List<Map<String, Object>> resultList = new ArrayList<>(mapList.size());
                for (Map<String, Object> objectMap : mapList) {
                    if (filter == null || !filter.accept(dbId, purchaserId, objectMap)) {
                        resultList.add(objectMap);
                    }
                }
                int validSize = resultList.size();
                log.info("查询 es 结果数量为: {} 条,有效的数量为: {} 条",size,validSize);
                log.info("查询 es 结果 耗时:{} ms",System.currentTimeMillis()-startTime);
                return resultList;
            }
            log.info("查询 es 结果数量为: {} 条, 耗时:{} ms",size,System.currentTimeMillis()-startTime);
            return mapList;
        } catch (Exception e) {
            log.error(" 查询 es 出现异常!!! 数据库 dbId: {}",dbId,e);
        }
        return null;
    }

    @Override
    protected Map getBy(String index, String type, DefaultGetDelRequest searchRequest, String purchaserId, Filter filter) {
        long startTime = System.currentTimeMillis();
        if (StringUtils.isEmpty(index)) {
            throw new RuntimeException("index 不能为空!!!");
        }
        if (StringUtils.isEmpty(type)) {
            type = GlobalConstant.HIUP_PERSON_TYPE;
        }
        searchRequest.setIndex(index);
        searchRequest.setType(type);
        String dbId = getDbIdByIndex(index);
        try {
            Map<String, Object> objectMap = elasticsearchTemplate.get(searchRequest);
            log.info("查询 es 结果 耗时:{} ms",System.currentTimeMillis()-startTime);
            if ((filter == null)) {
                return objectMap;
            }
            if((StringUtils.isNotEmpty(dbId) && !filter.accept(dbId, purchaserId, objectMap))){
                return objectMap;
            }
        } catch (Exception e) {
            log.error(" 查询 es 出现异常!!! 数据库 dbId: {}",dbId,e);
        }
        return null;
    }

    private static final String PREFIX = "person_";
    private String getDbIdByIndex(String index) {
        String esSufffix = index.substring(index.indexOf(PREFIX)+PREFIX.length());
        EntityWrapper<TbDbResource> wrapper = new EntityWrapper<>();
        wrapper.eq("ES_SUFFIX",esSufffix);
        TbDbResource tbDbResource = tbDbResourceService.selectOne(wrapper);
        if(tbDbResource != null){
            String resourceDbId = tbDbResource.getDbId();
            return resourceDbId;
        }
        return null;
    }

}
