package com.gijun.salesmanagement.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, "E401");
    }
}
