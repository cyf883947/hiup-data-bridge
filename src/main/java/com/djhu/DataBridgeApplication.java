package com.djhu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:application.properties","classpath:jdbc.properties"})
//@PropertySource(value = {"file:application.properties","file:jdbc.properties"}, encoding = "UTF-8")
public class DataBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBridgeApplication.class, args);
    }

}
