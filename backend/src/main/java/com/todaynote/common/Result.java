package com.todaynote.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 统一接口响应体。
 *
 * @param <T> 响应数据类型
 */
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    /**
     * 构建成功响应并返回业务数据。
     *
     * @param data 业务数据
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, "成功", data);
    }
    
    /**
     * 构建无数据的成功响应。
     *
     * @return 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, "成功", null);
    }

    /**
     * 构建默认系统错误响应。
     *
     * @param message 错误信息
     * @return 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.SYSTEM_ERROR, message, null);
    }
    
    /**
     * 构建带业务错误码的失败响应。
     *
     * @param code 错误码
     * @param message 错误信息
     * @return 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
