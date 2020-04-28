package com.djhu.service.receive;

import com.djhu.service.push.IQueryAndPushService;
import com.djhu.service.push.ProvideAndSendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2020-04-27 18:32
 **/
@Slf4j
@Component
//监听的队列名称
@RabbitListener(queues = "${djhu.rabbit.queue}")
public class CreateOrUpdateReceive {

    @Autowired
    IQueryAndPushService queryAndPushService;

    @RabbitHandler
    public void process(String testMessage) {
        log.info("创建数据库接受到消息为 : {}", testMessage);

        String dbId = "";
        queryAndPushService.dispose(dbId,null, ProvideAndSendData.CREATE_OR_UPDATE);
    }

}
