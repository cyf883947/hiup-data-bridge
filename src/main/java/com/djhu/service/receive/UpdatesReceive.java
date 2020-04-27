package com.djhu.service.receive;

import com.djhu.entity.MsgInfo;
import com.djhu.service.IQueryAndPushService;
import com.djhu.service.push.ProvideAndSendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2020-04-27 18:34
 **/

@Slf4j
@Component
//监听的队列名称
@RabbitListener(queues = "${djhu.rabbit.queue}")
public class UpdatesReceive {

    @Autowired
    IQueryAndPushService queryAndPushService;

    @RabbitHandler
    public void process(String testMessage) {
        log.info("增量接受到消息为 : {}", testMessage);
        String dbId = "";
        MsgInfo msgInfo = new MsgInfo();
        queryAndPushService.dispose(dbId,msgInfo, ProvideAndSendData.ADD);
    }

}