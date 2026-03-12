package com.acasado.opored.exception;

public class AdministratorWithoutPermissionException extends RuntimeException {
    public AdministratorWithoutPermissionException(String message) {
        super(message);
    }
}
