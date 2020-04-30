package com.djhu;

import com.djhu.service.ProcessingData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 10:00
 **/
public class ProcessingDataTest extends Tester {

    @Autowired
    ProcessingData processingData;

    @Test
    public void test(){
        // 测试推送全部数据库
        processingData.handle();
    }

}
