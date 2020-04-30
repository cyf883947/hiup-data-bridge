package com.djhu.config;

import com.djhu.config.callback.ConfirmCallBack;
import com.djhu.config.callback.ReturnCallBack;
import com.djhu.config.properties.RabbitProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cyf
 * @description
 * @create 2019-11-22 16:02
 **/
@Configuration
@ConditionalOnBean(RabbitProperties.class)
public class RabbitMqConfig {

    @Autowired
    private RabbitProperties rabbitProperties;

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitProperties.getHost());
        factory.setPort(rabbitProperties.getPort());
        factory.setUsername(rabbitProperties.getUsername());
        factory.setPassword(rabbitProperties.getPassword());
        factory.setVirtualHost(rabbitProperties.getVirtualHost());
        // 消息发送到交换机确认机制 是否确认回调
        factory.setPublisherConfirms(true);
        // 消息发送到交换机确认机制 是否返回回调
        factory.setPublisherReturns(true);
        try {
            factory.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
    }

    @Autowired
    private ReturnCallBack returnCallBack;

    @Autowired
    private ConfirmCallBack confirmCallBack;

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(rabbitProperties.getExchange());
        // #设置为 true 后 消费者在消息没有被路由到合适队列情况下会被return监听，而不会自动删除
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(confirmCallBack);
        rabbitTemplate.setReturnCallback(returnCallBack);
        return rabbitTemplate;
    }

    @Bean
   public TopicExchange topicExchange() {
        return new TopicExchange(rabbitProperties.getExchange());
    }

    @Bean
    public Queue createQueue(){
        return new Queue(rabbitProperties.getCreateQueue(), rabbitProperties.isDurable());
    }

    @Bean
    public Queue updateQueue(){
        return new Queue(rabbitProperties.getUpdateQueue(), rabbitProperties.isDurable());
    }

    @Bean
    public Binding bindingCreateQueue(TopicExchange topicExchange) {
        return BindingBuilder.bind(createQueue()).to(topicExchange).with("topic.data.create");
    }

    @Bean
    public Binding bindingUpdateQueue(TopicExchange topicExchange) {
        return BindingBuilder.bind(updateQueue()).to(topicExchange).with("topic.data.update");
    }

}
