package com.gijun.salesmanagement.exception;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(message, "E403");
    }
}
