package com.acasado.opored.service;

import com.acasado.opored.dto.AdministratorDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.model.AdministratorEntity;
import com.acasado.opored.repository.AdministratorRepository;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.util.AdministratorFactory;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministratorServiceTest {

    @Mock
    private AdministratorRepository administratorRepository;

    @Mock
    private JpaUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministratorService administratorService;

    @Test
    void When_GetAllAdministrators_Expect_ListDTO() {
        // Arrange
        List<AdministratorEntity> entities = List.of(AdministratorFactory.createValidAdministratorEntity());
        when(administratorRepository.findAll()).thenReturn(entities);

        // Act
        List<AdministratorDTO> result = administratorService.getAllAdministrators();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getEmail(), result.getFirst().getEmail());
    }

    @Test
    void When_GetAdministratorById_Expect_DTO() {
        // Arrange
        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();
        when(administratorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        AdministratorDTO result = administratorService.getAdministratorById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_EntityNotFoundException_When_GetAdministratorById_NotFound() {
        // Arrange
        when(administratorRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> administratorService.getAdministratorById(999));
    }

    @Test
    void When_GetAdministratorByEmail_Expect_DTO() {
        // Arrange
        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();
        String email = "john.doe@example.com";
        when(administratorRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act
        AdministratorDTO result = administratorService.getAdministratorByEmail(email);

        // Assert
        assertEquals(email, result.getEmail());
    }

    @Test
    void When_GetMe_Expect_DTO() {
        // Arrange
        int currentUserId = 1;
        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            when(administratorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));

            // Act
            AdministratorDTO result = administratorService.getMe();

            // Assert
            assertEquals(entity.getEmail(), result.getEmail());
        }
    }

    @Test
    void When_CreateAdministrator_Expect_AuthResponse() {
        // Arrange
        AdministratorDTO inputDto = AdministratorFactory.createValidAdministratorDTO();
        AuthResponse expectedResponse = AdministratorFactory.createAuthResponse();

        when(userDetailsService.createUser(any(AuthCreateUserRequest.class))).thenReturn(expectedResponse);

        // Act
        AuthResponse result = administratorService.createAdministrator(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getAccessToken(), result.getAccessToken());

        // Verify correct role mapping
        ArgumentCaptor<AuthCreateUserRequest> captor = ArgumentCaptor.forClass(AuthCreateUserRequest.class);
        verify(userDetailsService).createUser(captor.capture());
        assertEquals("ADMIN", captor.getValue().getRole());
    }

    @Test
    void When_UpdateMe_Expect_UpdatedDTO() {
        // Arrange
        int currentUserId = 1;
        String newName = "AdministratorUpdated";
        String newSurname = "UserUpdated";
        String newPass = "newEncodedPass12A@d";

        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();
        UserUpdateRequest request = AdministratorFactory.createUserUpdateRequest();

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            securityUtilsMock.when(() -> SecurityUtils.passwordValidation(anyString())).thenReturn(true);

            when(administratorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));
            when(administratorRepository.save(any(AdministratorEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            AdministratorDTO result = administratorService.updateMe(request);

            // Assert
            assertEquals(newName, result.getName());
            assertEquals(newSurname, result.getSurname());

            verify(passwordEncoder).encode(newPass);
            verify(administratorRepository).save(entity);
        }
    }

    @Test
    void When_DeleteAdministrator_Expect_LogicalDelete() {
        // Arrange
        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();
        when(administratorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        administratorService.deleteAdministrator(1);

        // Assert
        assertTrue(entity.getIsDeleted());
        assertFalse(entity.isEnabled());
        verify(administratorRepository).save(entity);
    }

    @Test
    void When_DeleteMe_Expect_LogicalDelete() {
        // Arrange
        int currentUserId = 1;
        AdministratorEntity entity = AdministratorFactory.createValidAdministratorEntity();

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            when(administratorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));

            // Act
            administratorService.deleteMe();

            // Assert
            assertTrue(entity.getIsDeleted());
            assertFalse(entity.isEnabled());
            verify(administratorRepository).save(entity);
        }
    }
}