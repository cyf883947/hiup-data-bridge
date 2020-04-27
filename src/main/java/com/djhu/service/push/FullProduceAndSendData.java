//package com.djhu.service.push;
//
//import com.djhu.entity.MsgInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author cyf
// * @description     全部数据库推送数据（程序启动时加载）
// * @create 2020-04-26 10:02
// **/
//@Slf4j
//@Service(value = "fullProduceAndSendData")
//public class FullProduceAndSendData extends AbstractProvideAndSendData {
//
//    @Override
//    protected List produce(String dbId, Integer pushType, MsgInfo msgInfo) {
//        // 查当前库es数据 todo 考虑数据量问题如何推送
//        List list = queryDataService.findAll(dbId);
//        log.info("程序启动-首次需要推送的数据条数为: {} 条，数据库dbId: {}",list.size(),dbId);
//        return list;
//    }
//
//    @Override
//    protected boolean isProduce(String dbId) {
//        // 推送全部数据库是在程序启动时调用，所以针对的是第一次推送，已经推送过的数据不再执行推送
//        return !existRecord(dbId);
//    }
//}
