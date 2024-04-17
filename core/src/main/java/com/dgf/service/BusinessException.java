package com.dgf.service;

public class BusinessException extends RuntimeException {

    public static final String UNHANDLED_BUSINESS_EXCEPTION = "unhandled.businessException";

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(Exception e) {
        super(UNHANDLED_BUSINESS_EXCEPTION, e);
    }

}
