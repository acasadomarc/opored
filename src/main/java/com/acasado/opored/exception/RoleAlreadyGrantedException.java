package com.acasado.opored.exception;

public class RoleAlreadyGrantedException extends RuntimeException {
    public RoleAlreadyGrantedException(String message) {
        super(message);
    }
}
