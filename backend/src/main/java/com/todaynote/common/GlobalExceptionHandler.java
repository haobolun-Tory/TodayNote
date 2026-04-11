package com.todaynote.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
/**
 * 全局异常处理器，负责把后端异常统一转换为标准响应结构。
 */
public class GlobalExceptionHandler {

    /**
     * 处理业务异常，直接透传业务错误码和错误信息。
     *
     * @param e 业务异常
     * @return 标准失败响应
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理基于请求体的参数校验异常。
     *
     * @param e 参数校验异常
     * @return 标准失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        return Result.error(ResultCode.VALIDATION_ERROR, extractMessage(e.getBindingResult(), "请求参数校验失败"));
    }

    /**
     * 处理查询参数或表单绑定异常。
     *
     * @param e 绑定异常
     * @return 标准失败响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        return Result.error(ResultCode.VALIDATION_ERROR, extractMessage(e.getBindingResult(), "请求参数校验失败"));
    }

    /**
     * 处理路径参数或查询参数类型不匹配异常。
     *
     * @param e 类型转换异常
     * @return 标准失败响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数 %s 格式不正确", e.getName());
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理请求体格式不正确的异常。
     *
     * @param e 请求体异常
     * @return 标准失败响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadableException(HttpMessageNotReadableException e) {
        return Result.error(ResultCode.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 处理显式抛出的非法参数异常。
     *
     * @param e 非法参数异常
     * @return 标准失败响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(ResultCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * 兜底处理所有未显式捕获的异常。
     *
     * @param e 未知异常
     * @return 标准失败响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return Result.error(ResultCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }

    /**
     * 提取第一个字段错误信息，避免前端拿到冗长异常堆栈。
     *
     * @param bindingResult 绑定结果
     * @param defaultMessage 默认提示
     * @return 可直接展示的错误信息
     */
    private String extractMessage(BindingResult bindingResult, String defaultMessage) {
        if (bindingResult == null || bindingResult.getFieldError() == null) {
            return defaultMessage;
        }
        return Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
    }
}
