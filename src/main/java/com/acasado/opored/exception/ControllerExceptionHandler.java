package com.acasado.opored.exception;

import com.acasado.opored.dto.ApiErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(StudentWithoutPermissionException.class)
    public ResponseEntity<ApiErrorDTO> handleStudentWithoutPermission(StudentWithoutPermissionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "The student does not have permission to modify other users' resources: " + ex.getMessage()));
    }

    @ExceptionHandler(ModeratorWithoutPermissionException.class)
    public ResponseEntity<ApiErrorDTO> handleModeratorWithoutPermission(ModeratorWithoutPermissionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "The moderator does not have permission to modify other users' resources: " + ex.getMessage()));
    }

    @ExceptionHandler(AdministratorWithoutPermissionException.class)
    public ResponseEntity<ApiErrorDTO> handleAdministratorWithoutPermission(AdministratorWithoutPermissionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "The administrator does not have permission to modify other users' resources: " + ex.getMessage()));
    }

    @ExceptionHandler(ProfessorWithoutPermissionException.class)
    public ResponseEntity<ApiErrorDTO> handleProfessorWithoutPermission(ProfessorWithoutPermissionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "The professor does not have permission to modify other users' resources: " + ex.getMessage()));
    }

    @ExceptionHandler(UserWithoutPermissionException.class)
    public ResponseEntity<ApiErrorDTO> handleUserWithoutPermission(UserWithoutPermissionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "The user does not have permission to modify other users' resources: " + ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, "You do not have permission to perform this action:  " + ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiErrorDTO> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(RestrictedDeleteException.class)
    public ResponseEntity<ApiErrorDTO> handleRestrictedDelete(RestrictedDeleteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(RoleAlreadyGrantedException.class)
    public ResponseEntity<ApiErrorDTO> handleRoleAlreadyGranted(RoleAlreadyGrantedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationException(HandlerMethodValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(HttpStatus.BAD_REQUEST, "Validation failure: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + ex.getMessage()));
    }

    private ApiErrorDTO error(HttpStatus status, String message) {
        return ApiErrorDTO.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }
}