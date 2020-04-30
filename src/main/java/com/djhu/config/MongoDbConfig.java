package com.djhu.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zw on 2018/1/26.
 *
 * @author zw
 */
@Slf4j
@Configuration
public class MongoDbConfig {

    @Value("${djhu.mongo.host}")
    private String mongo_host;

    @Value("${djhu.mongo.port}")
    private Integer mongo_port;

    @Value("${djhu.mongo.dbname}")
    private String mongo_dbname;

    @Value("${djhu.mongo.username:#{null}}")
    private String username;


    @Value("${djhu.mongo.password:#{null}}")
    private String password;

    @Bean
    public MongoClient mongo() throws UnknownHostException {
        ServerAddress serverAddress = new ServerAddress(mongo_host, mongo_port);
        List<MongoCredential> mongoCredentialList = new ArrayList<>();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
            mongoCredentialList.add(MongoCredential.createCredential(username, mongo_dbname,
                    password.toCharArray()));
        }
        MongoClient mongoClient = null;
        if (CollectionUtils.isEmpty(mongoCredentialList)){
            mongoClient = new MongoClient(serverAddress);
        }else {
            mongoClient = new MongoClient(serverAddress,mongoCredentialList);
        }
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        return mongoClient;
    }


    @Bean
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {
		return new SimpleMongoDbFactory(mongo(),mongo_dbname);
	}

    @Bean
	public MappingMongoConverter mappingMongoConverter() throws UnknownHostException {
		MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()), new MongoMappingContext());
		mappingMongoConverter.setMapKeyDotReplacement("\\+");
		return mappingMongoConverter;
	}

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongoDbFactory(),mappingMongoConverter());
    }
}