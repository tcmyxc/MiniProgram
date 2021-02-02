package com.tcmyxc.exception;

import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 * @author 徐文祥
 * @date 2021/1/15 0:16
 */

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandlder {

    @ExceptionHandler(value = Exception.class)// 拦截所有异常
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        // 绑定异常
        if(e instanceof BindException){
            // BindException 导包要正确
            // import org.springframework.validation.BindException;
            BindException bindException = (BindException) e;
            // 获取异常信息
            List<ObjectError> errors = bindException.getAllErrors();
            String message = errors.get(0).getDefaultMessage();// 获取第一个参数的的信息
            return Result.error(CodeMsg.BIND_EXCEPTION.fillArgs(message));
        }
        else if(e instanceof GlobalException){
            GlobalException globalException = (GlobalException)e;
            // 返回错误信息
            return Result.error(globalException.getcodeMsg());
        }
        // 其他异常
        else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
