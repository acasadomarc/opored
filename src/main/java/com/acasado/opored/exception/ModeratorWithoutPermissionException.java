package com.acasado.opored.exception;

public class ModeratorWithoutPermissionException extends RuntimeException {
    public ModeratorWithoutPermissionException(String message) {
        super(message);
    }
}
