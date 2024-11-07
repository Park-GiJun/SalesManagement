package com.gijun.salesmanagement.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException {
    @Getter
    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

