package com.acasado.opored.exception;

public class ProfessorWithoutPermissionException extends RuntimeException {
    public ProfessorWithoutPermissionException(String message) {
        super(message);
    }
}
