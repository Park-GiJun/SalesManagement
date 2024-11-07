package com.gijun.salesmanagement.exception;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(String message) {
        super(message, "E400");
    }
}
