package com.djhu.common.aspect;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author zw
 * @DATE 2019/6/5 9:22
 * @VERSION 1.0.0
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {
    public String value();

    public boolean saveLog() default false; // 是否开启记录日志

    public LogTypeEnum logType() default LogTypeEnum.DEFAULT; // 操作类型

    public boolean printLog() default true; // 是否打印日志

}
