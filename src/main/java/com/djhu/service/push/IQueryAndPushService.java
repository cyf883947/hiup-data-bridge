package com.djhu.service.push;

import com.djhu.entity.MsgInfo;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 17:13
 **/
public interface IQueryAndPushService {

    /**
     *  0-全部 1-创建或更新 2-增量
     */
    Integer ALL = 0;
    Integer CREATE_OR_UPDATE = 1;
    Integer ADD = 2;


    void dispose(String dbId, MsgInfo msgInfo, Integer pushType);

}
