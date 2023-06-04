package com.atguigu.exception;

import com.atguigu.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public Result Error(Exception e) {
        return Result.fail().message("执行全局异常处理");
    }
}
