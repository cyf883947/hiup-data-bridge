package com.djhu.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description
 * @create 2019-11-29 16:04
 **/
@Data
@Component
@ConditionalOnProperty( name = {"djhu.rabbit.enabled"},
        havingValue = "true",
        matchIfMissing = false
)
@ConfigurationProperties(prefix = "djhu.rabbit")
public class RabbitProperties {
    private static final Integer DEFAULT_RABBIT_PORT = 5672;
    private static final String DEFAULT_RABBIT_HOST = "localhost";

    private String host;
    private Integer port;
    private String virtualHost;
    private String username;
    private String password;
    private String exchange ;

    private String createQueue;
    private String updateQueue;

    /**
     *  是否持久化
     */
    private boolean durable;

}
