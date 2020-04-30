package com.djhu.common.constant;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 9:26
 **/
public interface DataConst {

    /**
     * 数据库的可用状态 2-为已完成创建的数据库
     */
    String DATASOURCE_ENABLED_STATUS = "2";
    /**
     * 全院库的状态，推送数据不推送全院库数据
     */
    String QUAN_YUAN_KU_STATUS = "1";

    /**
     *  0-全部 1-创建或更新 2-增量
     */
    Integer ALL = 0;
    Integer CREATE_OR_UPDATE = 1;
    Integer INCREMENT = 2;
}
