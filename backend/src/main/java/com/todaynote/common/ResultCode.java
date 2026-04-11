package com.todaynote.common;

public final class ResultCode {

    private ResultCode() {
    }

    public static final int SUCCESS = 0;
    public static final int BAD_REQUEST = 40000;
    public static final int VALIDATION_ERROR = 40001;
    public static final int UNAUTHORIZED = 40100;
    public static final int FORBIDDEN = 40300;
    public static final int NOT_FOUND = 40400;
    public static final int CONFLICT = 40900;
    public static final int SYSTEM_ERROR = 50000;
}