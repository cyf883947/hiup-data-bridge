package com.djhu;

import com.djhu.service.ProcessingData;
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
    private ProcessingData processingData;

    @Value("${auto.push.enabled:false}")
    private boolean autoPush;

    @Override
    public void run(String... args) throws Exception {
        log.info("项目启动成功!!!");
//        if(autoPush){
//            log.info("启动推送开始!!!");
//            long startTime = System.currentTimeMillis();
//            log.info("启动推送结束!!! 耗时：{} ms",System.currentTimeMillis()-startTime);
//        }

        // 测试全部数据库
//        processingData.handle();
        // 测试建库、更新库-0 ，参数正常
//        processingData.handle("fe69b58c711e491daec6b9acdf099662", DataConst.CREATE_OR_UPDATE);
        // 测试建库、更新库-1 ，参数 dbId 异常
//        processingData.handle("556", DataConst.CREATE_OR_UPDATE);

//        // 测试增量-0 ，index 参数错误
//        processingData.handle("AXGboNtSL0SMcvORFmGB","hiup_research_person_100","hiup_research_person",DataConst.INCREMENT);
//        // 测试增量-1 ，id  参数错误
//        processingData.handle("AXGboNtSL0SMcvORFm","hiup_research_person_10005","hiup_research_person",DataConst.INCREMENT);
//        // 测试增量-2 ，参数正常
//        processingData.handle("AXGboNtSL0SMcvORFmGB","hiup_research_person_10005","hiup_research_person",DataConst.INCREMENT);

          // 测试查询mongoDb 数据库
//        CreateDataInfo creaetEntity = new CreateDataInfo();
//        creaetEntity.setDbId("44d97c82f05d4986ac5ae645457db003");
//        creaetEntity.setHost("192.168.130.192");
//        creaetEntity.setPort(27017);
//        creaetEntity.setCollection("research_1575874104717_16");
//        creaetEntity.setDbname("data_col");
//        processingData.handle("44d97c82f05d4986ac5ae645457db003", DataConst.CREATE_OR_UPDATE,creaetEntity);
    }
}
