package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

//import org.springframework.security.access.AccessDeniedException;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 全局异常处理器
 * @date 2023/1/18
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    //处理XueChengPlusException异常
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse doXueChengPlusException(XueChengPlusException e) {
        log.error("捕获异常信息："+e.getErrMessage());
        e.printStackTrace();
        String errMessage = e.getErrMessage();
        return new RestErrorResponse(errMessage);
    }
    // 捕获没有访问权限异常
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse doException(Exception e) {
        log.error("捕获异常信息："+e.getMessage());
        e.printStackTrace();
        if(e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("没有操作此功能的权限");
        }
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse doMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errors = new StringBuilder();
        fieldErrors.forEach(error->{
            errors.append(error.getDefaultMessage()).append(",");
        });
        log.error("捕获异常信息："+errors.toString());
        return new RestErrorResponse(errors.toString());
    }
    //  捕获没有访问权限异常
//    @ResponseBody
//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public RestErrorResponse doAccessDeniedException(AccessDeniedException e) {
//        log.error("无访问权限："+e.getMessage());
//        e.printStackTrace();
//        RestErrorResponse errorResponse = new RestErrorResponse("没有操作此功能的权限");
//        System.out.println("================================================");
//        System.out.println(errorResponse);
//        System.out.println("================================================");
//        return errorResponse;
//    }
}
