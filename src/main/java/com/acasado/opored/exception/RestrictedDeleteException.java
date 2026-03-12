package com.acasado.opored.exception;

public class RestrictedDeleteException extends RuntimeException {
    public RestrictedDeleteException(String message) {
        super(message);
    }
}
