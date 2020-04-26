package com.djhu.service.push;

import com.djhu.entity.MsgInfo;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 9:45
 **/
public interface ProvideAndSendData{

    /**
     * 数据库的可用状态 2-为已完成创建的数据库
     */
    String DATASOURCE_ENABLED_STATUS = "2";
    /**
     * 全院库的状态，推送数据不推送全院库数据
     */
   String QUAN_YUAN_KU_STATUS = "1";
    /**
     *  全量推送
     */
    Integer ALL = 0;
    /**
     *  新建库或更新库
     */
    Integer CREATE_OR_UPDATE = 1;
    /**
     *  增量更新
     */
    Integer ADD = 2;

    // 推送新建数据库患者 - 通过mq接受数据
    void push(String dbId,Integer pushType);

    // 推送增量更新患者- 通过mq接受数据
    void push(String dbId, MsgInfo msgInfo, Integer pushType);

    // 推送全部患者 - 项目启动进行推送
    void push(Integer pushType);


}
