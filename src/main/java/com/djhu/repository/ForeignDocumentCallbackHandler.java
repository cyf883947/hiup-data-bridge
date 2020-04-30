package com.djhu.repository;

import com.djhu.service.ISendData;
import com.djhu.service.ITbDbPurchaserRecordService;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;

import java.util.Arrays;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 14:29
 **/
@Slf4j
@Data
public class ForeignDocumentCallbackHandler implements DocumentCallbackHandler {

    private String url;
    private ITbDbPurchaserRecordService purchaserRecordService;
    private ISendData sendData;

    @Override
    public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
        /**
         *  1.查询出数据，将结果执行推送
         *  2.记录日志
         */
        log.info("----------------------------------------------------------------");
        log.info("查询到的数据为 object {}",dbObject);

        // 判断是否需要过滤

        sendData.send(Arrays.asList(dbObject),url);

        purchaserRecordService.insert(null);
    }
}
