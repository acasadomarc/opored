package com.acasado.opored.exception;

import com.acasado.opored.dto.ApiErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Test
    void Test_HandleEntityNotFound() {
        ResponseEntity<ApiErrorDTO> response = handler.handleEntityNotFound(new EntityNotFoundException("Not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void Test_HandleStudentWithoutPermission() {
        ResponseEntity<ApiErrorDTO> response = handler.handleStudentWithoutPermission(new StudentWithoutPermissionException("No permission"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("No permission"));
    }

    @Test
    void Test_HandleModeratorWithoutPermission() {
        ResponseEntity<ApiErrorDTO> response = handler.handleModeratorWithoutPermission(new ModeratorWithoutPermissionException("No permission"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void Test_HandleAdministratorWithoutPermission() {
        ResponseEntity<ApiErrorDTO> response = handler.handleAdministratorWithoutPermission(new AdministratorWithoutPermissionException("No permission"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void Test_HandleProfessorWithoutPermission() {
        ResponseEntity<ApiErrorDTO> response = handler.handleProfessorWithoutPermission(new ProfessorWithoutPermissionException("No permission"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void Test_HandleUserWithoutPermission() {
        ResponseEntity<ApiErrorDTO> response = handler.handleUserWithoutPermission(new UserWithoutPermissionException("No permission"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void Test_HandleEmailAlreadyRegistered() {
        ResponseEntity<ApiErrorDTO> response = handler.handleEmailAlreadyRegistered(new EmailAlreadyRegisteredException("Exists"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void Test_HandleRestrictedDelete() {
        ResponseEntity<ApiErrorDTO> response = handler.handleRestrictedDelete(new RestrictedDeleteException("Restricted"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void Test_HandleRoleAlreadyGranted() {
        ResponseEntity<ApiErrorDTO> response = handler.handleRoleAlreadyGranted(new RoleAlreadyGrantedException("Already granted"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void Test_HandleRefreshTokenExpired() {
        ResponseEntity<ApiErrorDTO> response = handler.handleRefreshTokenExpiredException(new RefreshTokenExpiredException("Expired"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void Test_HandleGeneralException() {
        ResponseEntity<ApiErrorDTO> response = handler.handleGeneralException(new RuntimeException("Error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
