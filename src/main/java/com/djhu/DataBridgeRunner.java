package com.djhu;

import com.djhu.service.ProcessingData;
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
    private IQueryAndPushService queryAndPushService;
    @Autowired
    private ProcessingData processingData;

    @Value("${auto.push.enabled:false}")
    private boolean autoPush;

    @Override
    public void run(String... args) throws Exception {
        log.info("项目启动成功!!!");
//        if(autoPush){
//            log.info("启动推送开始!!!");
//            long startTime = System.currentTimeMillis();
//            queryAndPushService.dispose("105a3294d3f84ac68c8be693657aafac",null, IQueryAndPushService.CREATE_OR_UPDATE);
//            log.info("启动推送结束!!! 耗时：{} ms",System.currentTimeMillis()-startTime);
//        }
//        processingData.handle();
//        processingData.handle("123", DataConst.CREATE_OR_UPDATE);
//        processingData.handle("123","hiup_research_person_9846",null, DataConst.INCREMENT);
    }
}
