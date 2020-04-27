package com.djhu.service;

import com.djhu.Tester;
import com.djhu.service.push.ProvideAndSendData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 15:01
 **/
@Slf4j
public class ProvideAndSendDataTest extends Tester {

    @Autowired
    @Qualifier(value = "fullProduceAndSendData")
    ProvideAndSendData fullProduceAndSendData;

    @Autowired
    @Qualifier(value = "createOrUpdateProduceAndSendData")
    ProvideAndSendData createOrUpdateProduceAndSendData;

    @Autowired
    @Qualifier(value = "incrementProduceAndSendData")
    ProvideAndSendData incrementProduceAndSendData;


    @Test
    public void test(){
//        fullProduceAndSendData
    }

}
