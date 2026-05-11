package com.example.campus.exception;

import com.example.campus.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleUnauthorizedException(UnauthorizedException e) {
        return ApiResponse.error(401, e.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handlePermissionDeniedException(PermissionDeniedException e) {
        return ApiResponse.error(403, e.getMessage());
    }

    // Catch typical business logic errors (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK) // Always return 200 OK
    public ApiResponse<Object> handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(500, e.getMessage());
    }

    // Catch other unhandled exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(999, "系统未知错误: " + e.getMessage());
    }
}
