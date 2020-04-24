package com.djhu.service;

import com.djhu.Tester;
import com.djhu.entity.dto.HIsInfoDto;
import com.djhu.service.query.IQueryDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        queryDataService.findAll(dbId);
    }

    @Test
    public void findAll2Test(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        HIsInfoDto hisinfo0 = new HIsInfoDto("0030495029", "ZY010030495029", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4");
        HIsInfoDto hisinfo1 = new HIsInfoDto("0030461794", "ZY010030461794", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4");
        List<HIsInfoDto> hIsInfos = Arrays.asList(hisinfo0,hisinfo1);
        queryDataService.findAll(dbId,hIsInfos);
    }

    @Test
    public void findByTest(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        queryDataService.findBy(dbId,new HIsInfoDto("0030495029", "ZY010030495029", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4"));
    }

}
