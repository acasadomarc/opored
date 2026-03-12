package com.acasado.opored.integration;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.repository.AdministratorRepository;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.service.PromoteUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class PromoteUserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PromoteUserService promoteUserService;

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void When_PromoteStudentToModerator_Expect_ModeratorRoleAndRecord() {
        // Arrange
        createRole(RoleEnum.MODERATOR);
        StudentEntity student = createStudent("promote-student@example.com");

        // Act
        promoteUserService.promoteToModerator(student.getId());

        // Assert
        assertTrue(moderatorRepository.existsById(student.getId()));
        assertEquals(RoleEnum.MODERATOR, studentRepository.findById(student.getId()).orElseThrow().getRole().getName());
    }

    @Test
    void When_DemoteModeratorToStudent_Expect_ModeratorRemoved() {
        // Arrange
        createRole(RoleEnum.MODERATOR);
        StudentEntity student = createStudent("demote-student@example.com");
        promoteUserService.promoteToModerator(student.getId());
        moderatorRepository.flush();

        // Act
        promoteUserService.demoteFromModerator(student.getId());

        // Assert
        assertFalse(moderatorRepository.existsById(student.getId()));
        assertEquals(RoleEnum.STUDENT, studentRepository.findById(student.getId()).orElseThrow().getRole().getName());
    }

    @Test
    void When_PromoteStudentToAdministrator_Expect_AdminRoleAndRecord() {
        // Arrange
        createRole(RoleEnum.ADMIN);
        StudentEntity student = createStudent("admin-student@example.com");

        // Act
        promoteUserService.promoteToAdministrator(student.getId(), RoleEnum.STUDENT.name());

        // Assert
        assertTrue(administratorRepository.existsById(student.getId()));
        assertEquals(RoleEnum.ADMIN, studentRepository.findById(student.getId()).orElseThrow().getRole().getName());
    }

    @Test
    void When_DemoteAdministratorToModerator_Expect_ModeratorRestored() {
        // Arrange
        createRole(RoleEnum.ADMIN);
        createRole(RoleEnum.MODERATOR);
        StudentEntity student = createStudent("demote-admin@example.com");
        promoteUserService.promoteToAdministrator(student.getId(), RoleEnum.STUDENT.name());

        // Act
        promoteUserService.demoteFromAdministrator(student.getId(), RoleEnum.MODERATOR.name());

        // Assert
        assertFalse(administratorRepository.existsById(student.getId()));
        assertTrue(moderatorRepository.existsById(student.getId()));
        assertEquals(RoleEnum.MODERATOR, studentRepository.findById(student.getId()).orElseThrow().getRole().getName());
    }
}