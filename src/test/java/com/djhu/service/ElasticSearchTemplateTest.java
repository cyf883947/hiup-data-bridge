package com.djhu.service;

import com.djhu.Tester;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.ElasticsearchTemplate;
import com.djhu.elasticsearch.core.request.ObjectAddRequest;
import com.djhu.elasticsearch.core.request.TermSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 15:35
 **/
@Slf4j
public class ElasticSearchTemplateTest extends Tester {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void test() throws Exception {
        TermSearchRequest searchRequest = new TermSearchRequest();
        searchRequest.setIndex(GlobalConstant.HIUP_PERSON_INDEX+"_9846");
        searchRequest.setType(GlobalConstant.HIUP_PERSON_TYPE);
        searchRequest.setField("his_id.keyword");
        searchRequest.setValue("0030497949");

        List<Map<String, Object>> mapList = elasticsearchTemplate.queryforList(searchRequest);
        for (Map<String, Object> objectMap : mapList) {
            ObjectAddRequest addRequest = new ObjectAddRequest();
            addRequest.setIndex(GlobalConstant.HIUP_PERSON_INDEX);
            addRequest.setType(GlobalConstant.HIUP_PERSON_TYPE);
            addRequest.setObject(objectMap);
            elasticsearchTemplate.add(addRequest);
        }
//        long count = elasticsearchTemplate.count(searchRequest);
//        log.info("count is {}",count);
    }

}
