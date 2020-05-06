package com.djhu.service.receive;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.djhu.entity.MsgInfo;
import com.djhu.entity.UpdateDataInfo;
import com.djhu.service.push.IQueryAndPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cyf
 * @description     增量更新监听队列
 * @create 2020-04-27 18:34
 **/

@Slf4j
@Component
@RabbitListener(queues = "${djhu.rabbit.updateQueue}")
public class UpdateDataReceive {

    @Autowired
    IQueryAndPushService queryAndPushService;

    @RabbitHandler
    public void process(String testMessage) throws ParseException {
        UpdateDataInfo updateDataInfo = JSON.parse(testMessage, UpdateDataInfo.class);
        log.info("增量接受到消息为 : {}", updateDataInfo);

        MsgInfo msgInfo = new MsgInfo();
        BeanUtils.copyProperties(updateDataInfo,msgInfo);
        // 通过 uniqueId 去查 dbId
//        String dbId = getDbIdByIndex(updateDataInfo.getIndex());
//        queryAndPushService.dispose(dbId,msgInfo, IQueryAndPushService.INCREMENT);
    }

    public static void main(String[] args) {
        String index = "hiup_research_person_9905";
        String prefix = "person_";
        String esSufffix = index.substring(index.indexOf(prefix)+prefix.length());
        System.out.println(esSufffix);
    }

}