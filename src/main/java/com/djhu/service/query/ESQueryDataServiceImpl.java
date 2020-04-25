package com.djhu.service.query;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.ElasticsearchTemplate;
import com.djhu.elasticsearch.core.RequestUtils;
import com.djhu.elasticsearch.core.request.*;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.entity.dto.HIsInfoDto;
import com.djhu.service.ITbDbResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 15:20
 **/
@Slf4j
@Service
public class ESQueryDataServiceImpl implements IQueryDataService<Map<String, Object>> {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ITbDbResourceService tbDbResourceService;

    @Override
    public List<Map<String, Object>> findAll(String dbId) {
        return findAll(dbId, Collections.emptyList());
    }

    @Override
    public Map<String, Object> findBy(String dbId, HIsInfoDto hIsInfo) {
        Map<String, Object> map = null;
        List<Map<String, Object>> mapList = hIsInfo == null?findAll(dbId):findAll(dbId, Arrays.asList(hIsInfo));
        if (CollectionUtils.isNotEmpty(mapList)) {
            map = mapList.get(0);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> findAll(String dbId, List<HIsInfoDto> hIsInfos) {
        SearchRequest searchRequest = getSearchRequest(hIsInfos);
        return findAll(dbId, searchRequest);
    }

    @Override
    public List<Map<String, Object>> findAll(String dbId, SearchRequest searchRequest) {
        String esSuffix = tbDbResourceService.getEsSuffixByDbId(dbId);
        if (StringUtils.isNotEmpty(esSuffix)) {
            String index = GlobalConstant.HIUP_PERSON_INDEX + "_" + esSuffix;
            String type = GlobalConstant.HIUP_PERSON_TYPE;
            ((AbstractRequest) searchRequest).setIndex(index);
            ((AbstractRequest) searchRequest).setType(type);
            List<Map<String, Object>> mapList = getMapList(dbId, searchRequest);
            return mapList;
        }
        return Collections.emptyList();
    }

    private SearchRequest getSearchRequest(List<HIsInfoDto> hIsInfos) {
        SearchRequest searchRequest;
        if (CollectionUtils.isEmpty(hIsInfos)) {
            searchRequest = new MacthAllRequest();
        } else {
            List<SearchRequest> searchRequests = new ArrayList<>(hIsInfos.size());
            Iterator<HIsInfoDto> it = hIsInfos.iterator();
            while (it.hasNext()) {
                HIsInfoRequest hIsInfoRequest = new HIsInfoRequest();
                BeanUtils.copyProperties(it.next(), hIsInfoRequest);
                AutoGeneratorSearchRequest autoSearchRequest = new AutoGeneratorSearchRequest(hIsInfoRequest);
                searchRequests.add(new ShouldSearchRequest(autoSearchRequest));
            }
            searchRequest = new BoolSearchRequest(searchRequests);
        }
        return searchRequest;
    }

    private List<Map<String, Object>> getMapList(String dbId, SearchRequest searchRequest) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            log.info("查询专科库请求为: {},数据库id: {}", RequestUtils.request2josn(searchRequest), dbId);
            mapList = elasticsearchTemplate.queryforList(searchRequest);
            log.info("查询专科库数据成功. 数据总数为: {} 条", mapList.size());
        } catch (Exception e) {
            log.error("查询专科库数据失败!!! 专科库dbId: {},{}", dbId, e);
        }
        return mapList;
    }


}
