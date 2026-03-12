package com.acasado.opored.service;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.RoleAlreadyGrantedException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.util.PromoteUserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoteUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private ModeratorRepository moderatorRepository;
    @Mock private AdministratorRepository administratorRepository;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private PromoteUserService promoteUserService;

    // --- Promote to Moderator ---

    @Test
    void When_PromoteToModerator_Expect_Success() {
        // Arrange
        StudentEntity student = PromoteUserFactory.createStudentEntity();
        RoleEntity modRole = PromoteUserFactory.createRole(RoleEnum.MODERATOR);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(moderatorRepository.existsById(1)).thenReturn(false);
        when(roleRepository.getRoleByName(RoleEnum.MODERATOR)).thenReturn(modRole);

        // Act
        promoteUserService.promoteToModerator(1);

        // Assert
        verify(userRepository).save(student); // Saves updated role
        verify(moderatorRepository).insertModerator(1); // Native insert
    }

    @Test
    void Expect_RoleAlreadyGranted_When_PromoteToModerator_AlreadyMod() {
        // Arrange
        StudentEntity student = PromoteUserFactory.createStudentEntity();
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(moderatorRepository.existsById(1)).thenReturn(true);

        // Act & Assert
        assertThrows(RoleAlreadyGrantedException.class, () -> promoteUserService.promoteToModerator(1));
    }

    // --- Demote from Moderator ---

    @Test
    void When_DemoteFromModerator_Expect_Success() {
        // Arrange
        ModeratorEntity moderator = PromoteUserFactory.createModeratorEntity();
        RoleEntity studentRole = PromoteUserFactory.createRole(RoleEnum.STUDENT);

        when(moderatorRepository.findById(2)).thenReturn(Optional.of(moderator));
        when(studentRepository.existsById(2)).thenReturn(false); // Needs insert into student table
        when(roleRepository.getRoleByName(RoleEnum.STUDENT)).thenReturn(studentRole);

        // Act
        promoteUserService.demoteFromModerator(2);

        // Assert
        verify(studentRepository).insertStudent(2);
        verify(moderatorRepository).deleteModerator(2);
    }

    // --- Promote to Administrator ---

    @Test
    void When_PromoteStudentToAdministrator_Expect_Success() {
        // Arrange
        StudentEntity student = PromoteUserFactory.createStudentEntity();
        RoleEntity adminRole = PromoteUserFactory.createRole(RoleEnum.ADMIN);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(administratorRepository.existsById(1)).thenReturn(false);
        when(roleRepository.getRoleByName(RoleEnum.ADMIN)).thenReturn(adminRole);

        // Act
        promoteUserService.promoteToAdministrator(1, "STUDENT");

        // Assert
        verify(userRepository).save(student);
        verify(administratorRepository).insertAdministrator(1);
    }

    @Test
    void When_PromoteModeratorToAdministrator_Expect_Success() {
        // Arrange
        ModeratorEntity moderator = PromoteUserFactory.createModeratorEntity();
        RoleEntity adminRole = PromoteUserFactory.createRole(RoleEnum.ADMIN);

        when(moderatorRepository.findById(2)).thenReturn(Optional.of(moderator));
        when(administratorRepository.existsById(2)).thenReturn(false);
        when(roleRepository.getRoleByName(RoleEnum.ADMIN)).thenReturn(adminRole);

        // Act
        promoteUserService.promoteToAdministrator(2, "MODERATOR");

        // Assert
        verify(userRepository).save(moderator);
        verify(administratorRepository).insertAdministrator(2);
    }

    // --- Demote from Administrator ---

    @Test
    void When_DemoteAdminToStudent_Expect_SuccessAndCleanup() {
        // Arrange
        AdministratorEntity admin = PromoteUserFactory.createAdministratorEntity();
        RoleEntity studentRole = PromoteUserFactory.createRole(RoleEnum.STUDENT);

        when(administratorRepository.findById(3)).thenReturn(Optional.of(admin));
        when(studentRepository.existsById(3)).thenReturn(false); // Needs insert
        when(moderatorRepository.existsById(3)).thenReturn(true); // Was also mod, needs cleanup
        when(roleRepository.getRoleByName(RoleEnum.STUDENT)).thenReturn(studentRole);

        // Act
        promoteUserService.demoteFromAdministrator(3, "STUDENT");

        // Assert
        verify(administratorRepository).deleteAdministrator(3);
        verify(studentRepository).insertStudent(3);
        verify(moderatorRepository).deleteModerator(3); // Cleanup check
        verify(userRepository).save(admin);
    }

    @Test
    void When_DemoteAdminToModerator_Expect_Success() {
        // Arrange
        AdministratorEntity admin = PromoteUserFactory.createAdministratorEntity();
        RoleEntity modRole = PromoteUserFactory.createRole(RoleEnum.MODERATOR);

        when(administratorRepository.findById(3)).thenReturn(Optional.of(admin));
        when(moderatorRepository.existsById(3)).thenReturn(false); // Needs insert
        when(roleRepository.getRoleByName(RoleEnum.MODERATOR)).thenReturn(modRole);

        // Act
        promoteUserService.demoteFromAdministrator(3, "MODERATOR");

        // Assert
        verify(administratorRepository).deleteAdministrator(3);
        verify(moderatorRepository).insertModerator(3);
        verify(userRepository).save(admin);
    }

    @Test
    void Expect_Exception_When_DemoteAdmin_ToInvalidRole() {
        // Arrange, Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> promoteUserService.demoteFromAdministrator(3, "INVALID_ROLE")
        );
    }
}