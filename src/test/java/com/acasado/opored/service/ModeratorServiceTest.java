package com.acasado.opored.service;

import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.model.ModeratorEntity;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.util.ModeratorFactory;
import com.acasado.opored.security.SecurityUtils;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceTest {

    @Mock
    private ModeratorRepository moderatorRepository;

    @Mock
    private JpaUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private ModeratorService moderatorService;

    @Test
    void When_GetAllModerators_Expect_ListDTO() {
        // Arrange
        List<ModeratorEntity> entities = List.of(ModeratorFactory.createValidModeratorEntity());
        when(moderatorRepository.findAll()).thenReturn(entities);

        // Act
        List<ModeratorDTO> result = moderatorService.getAllModerators();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getEmail(), result.getFirst().getEmail());
    }

    @Test
    void When_GetModeratorById_Expect_DTO() {
        // Arrange
        ModeratorEntity entity = ModeratorFactory.createValidModeratorEntity();
        when(moderatorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        ModeratorDTO result = moderatorService.getModeratorById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetModeratorById_NotFound() {
        // Arrange
        when(moderatorRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> moderatorService.getModeratorById(999));
    }

    @Test
    void When_GetModeratorByEmail_Expect_DTO() {
        // Arrange
        ModeratorEntity entity = ModeratorFactory.createValidModeratorEntity();
        String email = "mod@example.com";
        when(moderatorRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act
        ModeratorDTO result = moderatorService.getModeratorByEmail(email);

        // Assert
        assertEquals(email, result.getEmail());
    }

    @Test
    void When_CreateModerator_Expect_AuthResponse() {
        // Arrange
        ModeratorDTO dto = ModeratorFactory.createValidModeratorDTO();
        AuthResponse expectedResponse = ModeratorFactory.createAuthResponse();
        when(userDetailsService.createPrivilegedUser(any(AuthCreateUserRequest.class))).thenReturn(expectedResponse);

        // Act
        AuthResponse result = moderatorService.createModerator(dto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getAccessToken(), result.getAccessToken());

        ArgumentCaptor<AuthCreateUserRequest> captor = ArgumentCaptor.forClass(AuthCreateUserRequest.class);
        verify(userDetailsService).createPrivilegedUser(captor.capture());
        assertEquals("MODERATOR", captor.getValue().getRole());
    }

    @Test
    void When_DeleteModerator_Expect_LogicalDelete() {
        // Arrange
        ModeratorEntity entity = ModeratorFactory.createValidModeratorEntity();
        when(moderatorRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        moderatorService.disableModerator(1);

        // Assert
        assertFalse(entity.isEnabled());
        verify(moderatorRepository).save(entity);
    }

    @Test
    void When_DeleteMe_Expect_LogicalDelete() {
        // Arrange
        int currentUserId = 1;
        ModeratorEntity entity = ModeratorFactory.createValidModeratorEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
            when(moderatorRepository.findById(currentUserId)).thenReturn(Optional.of(entity));

            // Act
            moderatorService.deleteMe();

            // Assert
            assertTrue(entity.getIsDeleted());
            assertFalse(entity.isEnabled());
            verify(moderatorRepository).save(entity);
        }
    }
}