package com.djhu.service;

import com.djhu.entity.CreateDataInfo;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 20:12
 **/
public interface ProcessingData {

    void handle();

    void handle(String dbId, Integer handleType);

    void handle(String dbId, Integer handleType, CreateDataInfo createDataInfo);

    void handle(String id, String index, String type, Integer handleType);

}
