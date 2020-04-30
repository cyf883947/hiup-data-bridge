package com.djhu.config.callback;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2019-11-28 18:42
 **/
@Component
public class ReturnCallBack implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("return exchange: " + exchange + ", routingKey: "
                + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
    }
}
