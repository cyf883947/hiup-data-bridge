package com.djhu.service.oom;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.djhu.entity.ResultEntity;
import com.djhu.service.ISendData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 19:17
 **/
@Slf4j
@Service
public class DefaultSendData implements ISendData {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void send(List list, String url) {
        if(CollectionUtils.isEmpty(list) || StringUtils.isEmpty(url)){
            log.info(" 需要推送的数据 list 为空或者 调用地址 url 为空!!!");
            return;
        }
        ResponseEntity<ResultEntity> responseEntity = restTemplate.postForEntity(url, list, ResultEntity.class);
        System.out.println(responseEntity);
    }

}
