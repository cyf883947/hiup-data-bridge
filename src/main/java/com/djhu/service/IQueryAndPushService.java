package com.djhu.service;

import com.djhu.entity.MsgInfo;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 17:13
 **/
public interface IQueryAndPushService {

    void dispose(String dbId, MsgInfo msgInfo, Integer pushType);

}
