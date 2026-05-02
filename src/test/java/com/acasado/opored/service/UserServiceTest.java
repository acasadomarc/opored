package com.acasado.opored.service;

import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.RefreshTokenRepository;
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
    private AdministratorService administratorService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
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
    void When_DisableUser_AsProfessor_Expect_ProfessorServiceCalled() {
        // Arrange
        ProfessorEntity professor = new ProfessorEntity();
        setupBaseUserFields(professor, 4, RoleEnum.PROFESSOR, "profAlias");
        when(userRepository.findById(4)).thenReturn(Optional.of(professor));

        // Act
        userService.disableUser(4);

        // Assert
        verify(professorService).disableProfessor(4);
    }

    @Test
    void When_EnableUser_AsProfessor_Expect_ProfessorServiceCalled() {
        // Arrange
        ProfessorEntity professor = new ProfessorEntity();
        setupBaseUserFields(professor, 4, RoleEnum.PROFESSOR, "profAlias");
        when(userRepository.findById(4)).thenReturn(Optional.of(professor));

        // Act
        userService.enableUser(4);

        // Assert
        verify(professorService).enableProfessor(4);
    }

    @Test
    void When_EnableUser_AsStudent_Expect_StudentServiceCalled() {
        // Arrange
        StudentEntity student = createMockStudent(5);
        when(userRepository.findById(5)).thenReturn(Optional.of(student));

        // Act
        userService.enableUser(5);

        // Assert
        verify(studentService).enableStudent(5);
    }

    @Test
    void When_UpdateMe_WithEmptyPassword_Expect_NoPasswordEncoding() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        UserUpdateRequest request = new UserUpdateRequest("Name", "Surname", student.getAlias(), "", null);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);
            securityUtilsMock.when(() -> SecurityUtils.publicUserAliasValidation(student.getAlias())).thenReturn(true);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(userRepository.save(any(UserEntity.class))).thenReturn(student);

            // Act
            userService.updateMe(request);

            // Assert
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Test
    void When_UpdateMe_AsPrivilegedUser_Expect_PrivilegedAliasValidation() {
        // Arrange
        ModeratorEntity moderator = createMockModerator();
        UserUpdateRequest request = new UserUpdateRequest("Name", "Surname", "modAlias", "ValidPass1@", null);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(3);
            securityUtilsMock.when(() -> SecurityUtils.privilegedUserAliasValidation("modAlias")).thenReturn(true);
            securityUtilsMock.when(() -> SecurityUtils.passwordValidation(anyString())).thenReturn(true);

            when(userRepository.findById(3)).thenReturn(Optional.of(moderator));
            when(userRepository.save(any(UserEntity.class))).thenReturn(moderator);

            // Act
            userService.updateMe(request);

            // Assert
            securityUtilsMock.verify(() -> SecurityUtils.privilegedUserAliasValidation("modAlias"));
            securityUtilsMock.verify(() -> SecurityUtils.publicUserAliasValidation(anyString()), never());
        }
    }

    @Test
    void When_DeleteMe_WithTopicsOnly_Expect_TransferTopics() {
        // Arrange
        StudentEntity student = createMockStudent(CURRENT_USER_ID);
        student.getTopics().add(new TopicEntity());

        UserEntity defaultUser = createMockStudent(DEFAULT_DELETED_USER_ID);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(CURRENT_USER_ID);

            when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(student));
            when(userRepository.findById(DEFAULT_DELETED_USER_ID)).thenReturn(Optional.of(defaultUser));

            // Act
            userService.deleteMe();

            // Assert
            verify(topicService).changeTopicsOwner(student.getTopics(), defaultUser);
            verify(studentService).deleteMe(student);
        }
    }

    @Test
    void When_DeleteMe_AsProfessor_Expect_ProfessorServiceDeleteMe() {
        // Arrange
        ProfessorEntity professor = new ProfessorEntity();
        setupBaseUserFields(professor, 6, RoleEnum.PROFESSOR, "profAlias");
        
        when(userRepository.findById(6)).thenReturn(Optional.of(professor));
        when(userRepository.findById(DEFAULT_DELETED_USER_ID)).thenReturn(Optional.of(createMockStudent(1)));

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(6);

            // Act
            userService.deleteMe();

            // Assert
            verify(professorService).deleteMe(professor);
        }
    }

    @Test
    void When_DeleteMe_AsAdmin_Expect_AdminServiceDeleteMe() {
        // Arrange
        AdministratorEntity admin = createMockAdmin();
        admin.setId(7);
        
        when(userRepository.findById(7)).thenReturn(Optional.of(admin));
        when(userRepository.findById(DEFAULT_DELETED_USER_ID)).thenReturn(Optional.of(createMockStudent(1)));

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(7);

            // Act
            userService.deleteMe();

            // Assert
            verify(administratorService).deleteMe(admin);
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