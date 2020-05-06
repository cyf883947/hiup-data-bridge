package com.djhu.service.receive;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSONObject;
import com.djhu.common.constant.DataConst;
import com.djhu.entity.CreateDataInfo;
import com.djhu.service.ProcessingData;
import com.djhu.service.push.IQueryAndPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description    新建或更新数据库监听队列
 * @create 2020-04-27 18:32
 **/
@Slf4j
@Component
@RabbitListener(queues = "${djhu.rabbit.createQueue}")
public class CreateDataReceive {

    @Autowired
    IQueryAndPushService queryAndPushService;

    @Autowired
    ProcessingData processingData;


    @RabbitHandler
    public void process(String creaetString) throws ParseException {
        CreateDataInfo createDataInfo = JSON.parse(creaetString, CreateDataInfo.class);
        log.info("创建数据库接受到消息为 : {}", createDataInfo);
        String dbId = createDataInfo.getDbId();
//        queryAndPushService.dispose(dbId,null, IQueryAndPushService.CREATE_OR_UPDATE);

        processingData.handle(dbId, DataConst.CREATE_OR_UPDATE,createDataInfo);
    }

    public static void main(String[] args) throws ParseException {
        CreateDataInfo createDataInfo = new CreateDataInfo();
        createDataInfo.setDbId("12333334666");

        String jsonString = JSONObject.toJSONString(createDataInfo);

        CreateDataInfo parse = JSON.parse(jsonString, CreateDataInfo.class);
        System.out.println(parse);
    }

}
