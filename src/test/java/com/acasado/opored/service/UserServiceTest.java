package com.acasado.opored.service;

import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.UserRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ModeratorService moderatorService;
    @Mock
    private StudentService studentService;
    @Mock
    private ProfessorService professorService;
    @Mock
    private TopicService topicService;
    @Mock
    private MessageService messageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final Integer CURRENT_USER_ID = 2;
    private static final Integer DEFAULT_DELETED_USER_ID = 1;

    @Test
    void When_GetAllUsers_Expect_OnlyNonAdminsInList() {
        // Arrange
        StudentEntity student = createMockStudent(2);
        AdministratorEntity admin = createMockAdmin();

        when(userRepository.findAll()).thenReturn(List.of(student, admin));

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("STUDENT", result.getFirst().getRole());
    }

    @Test
    void When_GetMe_Expect_UserDTO() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act
            UserDTO result = userService.getMe();

            // Assert
            assertNotNull(result);
            assertEquals(student.getId(), result.getId());
            assertEquals(student.getEmail(), result.getEmail());
        }
    }

    @Test
    void Expect_EntityNotFoundException_When_GetMe_NotFound() {
        // Arrange
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> userService.getMe());
        }
    }

    @Test
    void When_DisableUser_AsStudent_Expect_StudentServiceCalled() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

        // Act
        userService.disableUser(CURRENT_USER_ID);

        // Assert
        verify(studentService).disableStudent(CURRENT_USER_ID);
        verifyNoInteractions(moderatorService, professorService);
    }

    @Test
    void When_EnableUser_AsModerator_Expect_ModeratorServiceCalled() {
        // Arrange
        ModeratorEntity moderator = createMockModerator();
        when(userRepository.findById(3)).thenReturn(Optional.of(moderator));

        // Act
        userService.enableUser(3);

        // Assert
        verify(moderatorService).enableModerator(3);
        verifyNoInteractions(studentService, professorService);
    }

    @Test
    void When_UpdateMe_Expect_UserDTOAndSaved() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        student.setProfilePhoto("oldPhoto.png");

        UserUpdateRequest request = new UserUpdateRequest("NewName", "NewSurname", "newAlias", "ValidPass1@", "newPhoto.png");

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            securityUtilsMock.when(() -> SecurityUtils.publicUserAliasValidation("newAlias")).thenReturn(true);
            securityUtilsMock.when(() -> SecurityUtils.passwordValidation("ValidPass1@")).thenReturn(true);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(userRepository.findByAlias("newAlias")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("ValidPass1@")).thenReturn("encodedPass");
            when(userRepository.save(any(UserEntity.class))).thenReturn(student);

            // Act
            UserDTO result = userService.updateMe(request);

            // Assert
            assertNotNull(result);
            verify(storageService).delete("oldPhoto.png");

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            assertEquals("NewName", captor.getValue().getName());
            assertEquals("encodedPass", captor.getValue().getPassword());
            assertEquals("newPhoto.png", captor.getValue().getProfilePhoto());
        }
    }

    @Test
    void Expect_AliasAlreadyRegisteredException_When_UpdateMe_AliasExists() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        UserUpdateRequest request = new UserUpdateRequest("Name", "Surname", "takenAlias", "", null);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(userRepository.findByAlias("takenAlias")).thenReturn(Optional.of(createMockStudent(99)));

            // Act & Assert
            assertThrows(AliasAlreadyRegisteredException.class, () -> userService.updateMe(request));
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void Expect_BadCredentialsException_When_UpdateMe_InvalidPassword() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        UserUpdateRequest request = new UserUpdateRequest("Name", "Surname", student.getAlias(), "invalid", null);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            securityUtilsMock.when(() -> SecurityUtils.publicUserAliasValidation(student.getAlias())).thenReturn(true);
            securityUtilsMock.when(() -> SecurityUtils.passwordValidation("invalid")).thenReturn(false);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> userService.updateMe(request));
            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void When_DeleteMe_Expect_TransferAssetsAndLogicalDelete() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        student.getMessages().add(new MessageEntity());
        student.getTopics().add(new TopicEntity());

        UserEntity defaultUser = createMockStudent(DEFAULT_DELETED_USER_ID);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(userRepository.findById(DEFAULT_DELETED_USER_ID)).thenReturn(Optional.of(defaultUser));

            // Act
            userService.deleteMe();

            // Assert
            assertTrue(student.getRefreshTokens().isEmpty());
            verify(messageService).changeMessagesOwner(student.getMessages(), defaultUser);
            // topicService.changeTopicsOwner should NOT be called because it's an else-if in the code
            verify(topicService, never()).changeTopicsOwner(any(), any());
            verify(studentService).deleteMe(student);
        }
    }

    // --- Helper Methods ---

    private StudentEntity createMockStudent(Integer id) {
        StudentEntity student = new StudentEntity();
        setupBaseUserFields(student, id, RoleEnum.STUDENT, "studentAlias");
        return student;
    }

    private AdministratorEntity createMockAdmin() {
        AdministratorEntity admin = new AdministratorEntity();
        setupBaseUserFields(admin, 3, RoleEnum.ADMIN, "adminAlias");
        return admin;
    }

    private ModeratorEntity createMockModerator() {
        ModeratorEntity moderator = new ModeratorEntity();
        setupBaseUserFields(moderator, 3, RoleEnum.MODERATOR, "modAlias");
        return moderator;
    }

    private void setupBaseUserFields(UserEntity user, Integer id, RoleEnum roleEnum, String alias) {
        user.setId(id);
        user.setName("Name");
        user.setSurname("Surname");
        user.setAlias(alias);
        user.setEmail("user" + id + "@example.com");
        user.setPassword("encodedPass");
        user.setRegistrationDate(LocalDate.now());
        user.setEnabled(true);

        RoleEntity role = new RoleEntity();
        role.setName(roleEnum);
        user.setRole(role);

        user.setMessages(new HashSet<>());
        user.setTopics(new HashSet<>());
        user.setRefreshTokens(new HashSet<>());
    }
}