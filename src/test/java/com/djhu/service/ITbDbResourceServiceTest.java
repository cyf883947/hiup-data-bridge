package com.djhu.service;

import com.djhu.Tester;
import com.djhu.entity.scientper.TbDbResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 13:46
 **/
@Slf4j
public class ITbDbResourceServiceTest extends Tester {

    @Autowired
    private ITbDbResourceService tbDbResourceService;

    @Test
    public void selectByIdTest(){
        String id = "105a3294d3f84ac68c8be693657aafac";
        TbDbResource tbDbResource = tbDbResourceService.selectById(id);
        log.info("tbDbResource is '{}'",tbDbResource);

    }



}
