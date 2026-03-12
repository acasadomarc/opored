package com.acasado.opored.exception;

public class StudentWithoutPermissionException extends RuntimeException {
    public StudentWithoutPermissionException(String message) {
        super(message);
    }
}
