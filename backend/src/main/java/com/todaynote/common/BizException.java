package com.todaynote.common;

/**
 * 业务异常，用于在业务层显式抛出可预期错误。
 */
public class BizException extends RuntimeException {

    private final Integer code;

    /**
     * 创建业务异常。
     *
     * @param code 业务错误码
     * @param message 错误信息
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务错误码。
     *
     * @return 业务错误码
     */
    public Integer getCode() {
        return code;
    }
}
