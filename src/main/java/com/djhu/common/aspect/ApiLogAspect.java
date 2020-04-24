package com.djhu.common.aspect;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.djhu.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zw
 * @DATE 2019/6/5 9:21
 * @VERSION 1.0.0
 **/
@Slf4j
@Aspect
@Order(1)
@Component
@EnableAspectJAutoProxy
public class ApiLogAspect {

    @Pointcut("@annotation(com.djhu.common.aspect.ApiLog)")
    public void cutPoint() {
    }

    @Around(value = "cutPoint()&&@annotation(apiLog)")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint, ApiLog apiLog) throws Throwable {
        try {
            long start = System.currentTimeMillis();
            if (apiLog == null) {
                return joinPoint.proceed();
            }
            Object[] args = joinPoint.getArgs();
            Object logArgs = getLogArgs(args);
            log.info("{} 接口，接到的请求为 {}", apiLog.value(), JSON.toJSON(logArgs));
            Object obj = joinPoint.proceed();
            long end = System.currentTimeMillis();

            // 存储日志
            if (apiLog.saveLog()) {
                // doSave
            }

            String strLog;
            if (obj != null && (obj instanceof Result)) {
//                Result result = (Result) obj;
                strLog = String.format("%s 接口,用时为 %s ms", apiLog.value(), String.valueOf(end - start));
            } else{
                log.warn("Return value:" + obj.getClass().getName() + " is not instance of " + Result.class.getName() + " save api log may be error");
                strLog = String.format("%s 接口，返回界面的结果为 %s,用时为 %s", apiLog.value(), JSON.toJSON(obj), String.valueOf(end - start));
            }
            log.info(strLog);
            return obj;
        } catch (Throwable t) {
            log.error("接口 {} 出现异常！！！", apiLog.value(), t);
            throw t;
        } finally {

        }
    }

    private Object getLogArgs(Object[] args) {
        List list = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse) {
                continue;
            } else {
                list.add(arg);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                return list;
            }
        } else {
            return new ArrayList<>();
        }
    }
}
