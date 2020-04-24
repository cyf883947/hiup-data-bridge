package com.djhu.common.advice;

import com.djhu.common.Result;
import com.djhu.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 12:03
 **/
@ControllerAdvice
@Slf4j
public class CommonControlAdvice {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e) {
        return ResultGenerator.genFailResult(e.getMessage());
    }

}
