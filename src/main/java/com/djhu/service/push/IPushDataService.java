package com.djhu.service.push;

import com.djhu.entity.MsgInfo;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 17:56
 **/
public interface IPushDataService {

    // 推送新建数据库患者 - 通过mq接受数据
    void push(String dbId);

    // 推送增量更新患者- 通过mq接受数据
    void push(String dbId,MsgInfo msgInfo);

    // 推送全部患者 - 项目启动进行推送
    void push();

}
