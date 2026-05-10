package com.example.campus.exception;

import com.example.campus.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.error(403, "没有访问权限");
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleSessionExpiredException(SessionExpiredException e) {
        return ApiResponse.error(408, e.getMessage());
    }

    @ExceptionHandler(AccountDisabledException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleAccountDisabledException(AccountDisabledException e) {
        return ApiResponse.error(410, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(500, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(999, "系统未知错误: " + e.getMessage());
    }
}
