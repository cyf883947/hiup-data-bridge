package com.djhu.config.callback;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2019-11-28 18:41
 **/
@Component
public class ConfirmCallBack implements RabbitTemplate.ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        System.out.println("correlationData: " + correlationData);
        System.out.println("ack: " + ack);
        if (!ack) {
            System.out.println("HelloSender消息发送失败" + s + correlationData.toString());
        } else {
            System.out.println("HelloSender 消息发送成功 ");
        }
    }
}
