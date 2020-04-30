package com.djhu.repository;

import com.djhu.entity.CreateDataInfo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MongoTemplateFactory implements DisposableBean {

	@Autowired
	MappingMongoConverter mappingMongoConverter;

    private Map<String,MongoClient> mongoClientMap = new ConcurrentHashMap<>();

    public MongoTemplate getMongoTemplate(CreateDataInfo mongoTableInfo){
        String id = mongoTableInfo.getDbId();
        MongoClient mongoClient = null;
        if (mongoClientMap.containsKey(id)){
            log.debug("id is existed,id is {}",id);
            mongoClient = mongoClientMap.get(id);
        }else {
            try {
                mongoClient = new MongoClient(mongoTableInfo.getHost(),mongoTableInfo.getPort());
                mongoClient.setWriteConcern(WriteConcern.SAFE);
                log.debug("create a new id is {}",id);
                mongoClientMap.put(id,mongoClient);
            } catch (UnknownHostException e) {
                log.error(e.getMessage(),e);
                throw new RuntimeException(e);
            }
        }
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,mongoTableInfo.getDbname());
        MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(mongoClient,mongoTableInfo.getDbname()),mappingMongoConverter);
        return mongoTemplate;
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() throws Exception {
        mongoClientMap.forEach((id,client)->{
            log.info("start close client,id is {}",id);
            client.close();
        });
    }
}
