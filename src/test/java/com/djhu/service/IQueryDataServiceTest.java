package com.djhu.service;

import com.djhu.Tester;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.request.BoolSearchRequest;
import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.elasticsearch.core.request.ShouldSearchRequest;
import com.djhu.elasticsearch.core.request.TermSearchRequest;
import com.djhu.entity.dto.HIsInfoDto;
import com.djhu.service.query.IQueryDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 16:27
 **/
@Slf4j
public class IQueryDataServiceTest extends Tester {

    @Autowired
    private IQueryDataService queryDataService;

    @Test
    public void findAllTest(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        dbId = null;
        dbId = "24";
        queryDataService.findAll(dbId);
    }

    @Test
    public void findAll2Test(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        dbId = "a";
        HIsInfoDto hisinfo0 = new HIsInfoDto("0030495029", "ZY010030495029", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4");
        HIsInfoDto hisinfo1 = new HIsInfoDto("0030461794", "ZY010030461794", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4");
        List<HIsInfoDto> hIsInfos = Arrays.asList(hisinfo0,hisinfo1);
        queryDataService.findAll(dbId,hIsInfos);
    }

    @Test
    public void findByTest(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        dbId = null;
        queryDataService.findBy(dbId,new HIsInfoDto("0030495029", "ZY010030495029", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4"));
    }

    @Test
    public void findById2Test(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        String id = "AW_CKeFfL0SMcvOREW32";

//        id = "d889";
        dbId = "12";

        Object obj = queryDataService.findById(dbId, id);
        print(obj);
    }

    private void print(Object obj) {
        log.info("obj : {}",obj);
    }


    @Test
    public void findByIdTest(){
        // 参数正常时
        String index = GlobalConstant.HIUP_PERSON_INDEX+"_9846";
        String type = GlobalConstant.HIUP_PERSON_TYPE;
        String id = "AW_CKeFfL0SMcvOREW32";


        // inde 异常时
//        index = "abd";

        // id 异常时
        id = "中文";

        Object obj = queryDataService.findById(index, type, id);
        log.info("obj : {}",obj);
    }



    @Test
    public void findAll3Test(){
        String dbId;
        SearchRequest searchRequest;
        // dbId 异常时
        dbId = "db";

        // searchRequest 异常时
        searchRequest = new BoolSearchRequest(null);

        // 两个参数都正常时
        dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        searchRequest = getSearchRequest();

        List list = queryDataService.findAll(dbId, searchRequest);
        log.info("list is {}",list);
    }

    private SearchRequest getSearchRequest() {

        List<SearchRequest> searchRequestList = new ArrayList<>();
        TermSearchRequest termSearchRequest = new TermSearchRequest();
        termSearchRequest.setField("his_id.keyword");
        termSearchRequest.setValue("0030495029");
        searchRequestList.add(new ShouldSearchRequest(termSearchRequest));

        termSearchRequest = new TermSearchRequest();
        termSearchRequest.setField("his_id.keyword");
        termSearchRequest.setValue("0001261596");
        searchRequestList.add(new ShouldSearchRequest(termSearchRequest));

        termSearchRequest = new TermSearchRequest();
        termSearchRequest.setField("his_id.keyword");
        termSearchRequest.setValue("0030417267");
        searchRequestList.add(new ShouldSearchRequest(termSearchRequest));

        BoolSearchRequest boolSearchRequest = new BoolSearchRequest(searchRequestList);
        return boolSearchRequest;
    }

}
