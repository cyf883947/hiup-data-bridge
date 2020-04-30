package com.djhu.service;

import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 19:23
 **/
public interface Filter {

    boolean accept(String dbId,String purchaserId,Map map);
}
