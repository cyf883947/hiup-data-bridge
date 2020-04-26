package com.djhu;

import com.djhu.service.push.ProvideAndSendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 18:04
 **/
@Slf4j
@Component
public class DataBridgeRunner implements CommandLineRunner {

    @Autowired
    @Qualifier("fullProduceAndSendData")
    private ProvideAndSendData pushDataService;

    @Override
    public void run(String... args) throws Exception {
        log.info("项目启动成功!!!");
//        pushDataService.push(ProvideAndSendData.ALL);
    }
}
