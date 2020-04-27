package com.djhu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zw
 * @DATE 2019/4/26 16:25
 * @VERSION 1.0.0
 **/
@Slf4j
@Configuration
public class JmsConfig {

    @Value("${djhu.rabbit.host}")
    private String rabbitHost;
    @Value("${djhu.rabbit.port}")
    private Integer rabbitPort;
    @Value("${djhu.rabbit.virtualhost}")
    private String virtualHost;
    @Value("${djhu.rabbit.username}")
    private String rabbitUserName;
    @Value("${djhu.rabbit.password}")
    private String rabbitPassWord;
    @Value("${djhu.rabbit.exchange}")
    private String exchange;
    @Value("${djhu.rabbit.queue}")
    private String queueName;

    @Bean
    public RabbitTemplate rabbitTemplate() throws Exception {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        factory.setHost(rabbitHost);
        factory.setPort(rabbitPort);
        factory.setUsername(rabbitUserName);
        factory.setPassword(rabbitPassWord);
        factory.setVirtualHost(virtualHost);
        factory.afterPropertiesSet();
        com.rabbitmq.client.ConnectionFactory connectionFactory = factory.getObject();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setExchange(exchange);
        return rabbitTemplate;
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(exchange);
    }


    @Bean
    public Queue queue(){
        return new Queue(queueName);
    }

    @Bean
    public Binding bindingPersonInfoQueue(FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue()).to(fanoutExchange);
    }


}
