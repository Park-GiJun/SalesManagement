package com.gijun.salesmanagement.exception;

public class DuplicateEntityException extends BusinessException {
    public DuplicateEntityException(String message) {
        super(message, "E409");
    }
}
