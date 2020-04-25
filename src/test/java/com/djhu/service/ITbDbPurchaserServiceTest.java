package com.djhu.service;

import com.djhu.Tester;
import com.djhu.entity.atses.TbDbPurchaser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 13:48
 **/
@Slf4j
public class ITbDbPurchaserServiceTest extends Tester {

    @Autowired
    private ITbDbPurchaserService purchaserService;

    @Test
    public void selectByIdTest(){
        String id = "1";
        id = "a";
        TbDbPurchaser tbDbPurchaser = purchaserService.selectById(id);
        log.info("tbDbPurchaser is '{}'",tbDbPurchaser);
    }

}
