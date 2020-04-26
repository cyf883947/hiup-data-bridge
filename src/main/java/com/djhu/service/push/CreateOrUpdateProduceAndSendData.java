package com.djhu.service.push;

import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.entity.MsgInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description     创建或更新数据库时推送数据（通过mq接受消息）
 * @create 2020-04-26 10:05
 **/
@Slf4j
@Service(value = "createOrUpdateProduceAndSendData")
public class CreateOrUpdateProduceAndSendData extends AbstractProvideAndSendData {

    @Override
    protected List<Map<String, Object>> produce(String dbId, Integer pushType, MsgInfo msgInfo) {
        List resultList;
        if(existRecord(dbId)){
            // 获取指定条件的数据
            SearchRequest searchRequest = getSearchRequest(dbId);
            resultList = queryDataService.findAll(dbId, searchRequest);
        }else {
            // 直接获取数据
            resultList = queryDataService.findAll(dbId);
        }
        return resultList;
    }

    @Override
    protected boolean isProduce(String dbId) {
        // 创建或更新数据库范围，无论有没有推送记录都需要执行推送
        return true;
    }
}
