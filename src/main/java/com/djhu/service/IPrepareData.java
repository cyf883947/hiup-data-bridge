package com.djhu.service;

import com.djhu.entity.HIsInfoRequest;

import java.util.List;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 18:10
 **/
public interface IPrepareData<T> {

    /**
     *  准备专科库下的数据
     * @param dbId
     * @return
     */
    List<T> list(String dbId,String purchaserId,Filter filter);

    /**
     *  准备指定索引下的单条数据
     * @param id        es数据中的id
     * @param index     es索引index
     * @param type      es索引type
     * @return
     */
    T getBy(String id,String index,String type,String purchaserId,Filter filter);

    /**
     *  准备指定专科库下单条数据
     * @param dbId       数据库dbId
     * @param hIsInfo    患者的4个id
     * @return
     */
    T getBy(String dbId, HIsInfoRequest hIsInfo,String purchaserId,Filter filter);

}
