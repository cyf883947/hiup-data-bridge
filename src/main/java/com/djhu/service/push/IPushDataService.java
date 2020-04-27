//package com.djhu.service.push;
//
//import com.djhu.entity.MsgInfo;
//
///**
// * @author cyf
// * @description
// * @create 2020-04-24 17:56
// **/
//@Deprecated
//public interface IPushDataService {
//
//    /**
//     *  全量推送
//     */
//    Integer ALL = 0;
//    /**
//     *  新建库或更新库
//     */
//    Integer CREATE_OR_UPDATE = 1;
//    /**
//     *  增量更新
//     */
//    Integer ADD = 2;
//
//
//    // 推送新建数据库患者 - 通过mq接受数据
//    void push(String dbId,Integer pushType);
//
//    // 推送增量更新患者- 通过mq接受数据
//    void push(String dbId,MsgInfo msgInfo,Integer pushType);
//
//    // 推送全部患者 - 项目启动进行推送
//    void push(Integer pushType);
//
//}
