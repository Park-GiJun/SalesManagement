package com.gijun.salesmanagement.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message, "E404");
    }
}
