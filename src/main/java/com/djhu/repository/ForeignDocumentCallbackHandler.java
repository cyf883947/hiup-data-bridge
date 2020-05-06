package com.djhu.repository;

import com.djhu.service.Filter;
import com.djhu.service.ISendData;
import com.djhu.service.ITbDbPurchaserRecordService;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;

import java.util.Arrays;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 14:29
 **/
@Slf4j
@Data
public class ForeignDocumentCallbackHandler implements DocumentCallbackHandler {

    private Filter filter;
    private String url;
    private String dbId;
    private String purchasersId;
    private ITbDbPurchaserRecordService purchaserRecordService;
    private ISendData sendData;

    @Override
    public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
        Map<String, Object> map = (Map<String, Object>) dbObject;
        if (filter.accept(dbId, purchasersId, map)) {
            log.info("数据已推送过，不处理!!!");
        } else {
            sendData.send(Arrays.asList(map), url, dbId, purchasersId);
        }
    }
}
