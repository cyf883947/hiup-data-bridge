package com.djhu.service;

import com.djhu.Tester;
import com.djhu.common.constant.GlobalConstant;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.entity.dto.HIsInfoDto;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 18:55
 **/
public class IPrepareDataTest extends Tester {

    @Autowired
    IPrepareData prepareData;


    @Test
    public void test(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        List list = prepareData.list(dbId,null,null);
        System.out.println(list.size());
    }

    @Test
    public void test1(){
        String dbId = "39f4cf7bf1c34e04ac07ba017458ba50";
        HIsInfoDto hisinfo0 = new HIsInfoDto("0030495029", "ZY010030495029", "2.16.840.1.113883.4.487.2.1.4", "2.16.840.1.113883.4.487.2.1.4.4");
        HIsInfoRequest hIsInfoRequest = new HIsInfoRequest();
        BeanUtils.copyProperties(hisinfo0,hIsInfoRequest);
        Object obj = prepareData.getBy(dbId, hIsInfoRequest,null,null);
        System.out.println(obj);
    }

    @Test
    public void test2(){
        String id = "AW_CKeFfL0SMcvOREW32";
        String index = GlobalConstant.HIUP_PERSON_INDEX+"_9846";

        Object obj = prepareData.getBy(id,index,null,null,null);
        System.out.println(obj);
    }

}
