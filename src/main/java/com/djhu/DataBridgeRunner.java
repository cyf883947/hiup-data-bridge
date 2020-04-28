package com.djhu;

import com.djhu.service.push.IQueryAndPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auto.push.enabled:false")
    private boolean autoPush;

    @Override
    public void run(String... args) throws Exception {
        log.info("项目启动成功!!!");
        if(autoPush){
            log.info("启动推送开始!!!");
            long startTime = System.currentTimeMillis();
//            queryAndPushService.dispose(dbId,null,ProvideAndSendData.ADD);
            queryAndPushService.dispose(null,null, IQueryAndPushService.ALL);
            log.info("启动推送结束!!! 耗时：{} ms",System.currentTimeMillis()-startTime);
        }
    }
}
