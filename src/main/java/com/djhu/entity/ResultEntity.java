package com.djhu.entity;

import lombok.Data;

/**
 * @author cyf
 * @description
 * @create 2020-04-26 18:52
 **/
@Data
public class ResultEntity {
    private String msg;
    private Integer status = 200;
}
