package com.djhu.entity;

import lombok.Data;

/**
 * @author cyf
 * @description
 * @create 2020-04-28 15:55
 **/
@Data
public class CreateDataInfo {
    private String dbId;
    private String host;
    private int port;
    private String collection;
    private String dbname;
}
