package com.acasado.opored.exception;

public class UserWithoutPermissionException extends RuntimeException {
    public UserWithoutPermissionException(String message) {
        super(message);
    }
}
