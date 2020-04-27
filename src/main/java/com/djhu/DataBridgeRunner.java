package com.djhu;

import com.djhu.service.IQueryAndPushService;
import com.djhu.service.push.ProvideAndSendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @Qualifier("createOrUpdateProduceAndSendData")
    private IQueryAndPushService queryAndPushService;

    @Override
    public void run(String... args) throws Exception {
        log.info("项目启动成功!!!");
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        queryAndPushService.dispose(dbId,null,ProvideAndSendData.ADD);
    }
}
