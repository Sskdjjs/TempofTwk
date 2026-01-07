package com.example.demo.Controller;

// GlobalExceptionHandler.java

import com.example.demo.DTO.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.error(400, "参数错误: " + message);
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e, HttpServletRequest request) {
        log.error("接口异常: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResponse.error(500, "服务器错误: " + e.getMessage());
    }
}