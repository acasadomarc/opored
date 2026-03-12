package com.acasado.opored.repository;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.ModeratorEntity;
import com.acasado.opored.model.RoleEntity;
import com.acasado.opored.model.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private RoleEntity studentRole;
    private RoleEntity moderatorRole;

    @BeforeEach
    void setUp() {
        // Persist Roles needed for Foreign Keys
        studentRole = new RoleEntity();
        studentRole.setName(RoleEnum.STUDENT);
        studentRole.setPermissions(new HashSet<>());
        entityManager.persist(studentRole);

        moderatorRole = new RoleEntity();
        moderatorRole.setName(RoleEnum.MODERATOR);
        moderatorRole.setPermissions(new HashSet<>());
        entityManager.persist(moderatorRole);

        entityManager.flush();
    }

    @Test
    void whenFindByEmail_thenReturnStudent() {
        // Arrange
        StudentEntity student = new StudentEntity();
        student.setName("Jane");
        student.setSurname("Doe");
        student.setAlias("alias");
        student.setEmail("jane.student@test.com");
        student.setPassword("password");
        student.setRegistrationDate(LocalDate.now());
        student.setRole(studentRole);
        student.setEnabled(true);
        student.setIsDeleted(false);
        student.setAccountNoExpired(true);
        student.setAccountNoLocked(true);
        student.setCredentialNoExpired(true);

        entityManager.persist(student);
        entityManager.flush();

        // Act
        Optional<StudentEntity> found = studentRepository.findByEmail("jane.student@test.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jane");
    }

    @Test
    void whenInsertStudent_thenRowExistsInStudentsTable() {
        // Arrange:
        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setName("UserTo");
        moderator.setSurname("Demote");
        moderator.setAlias("alias");
        moderator.setEmail("demote.me@test.com");
        moderator.setPassword("1234");
        moderator.setRegistrationDate(LocalDate.now());
        moderator.setRole(moderatorRole);
        moderator.setEnabled(true);
        moderator.setIsDeleted(false);
        moderator.setAccountNoExpired(true);
        moderator.setAccountNoLocked(true);
        moderator.setCredentialNoExpired(true);

        ModeratorEntity savedModerator = entityManager.persist(moderator);
        entityManager.flush();
        Integer userId = savedModerator.getId();

        // Pre-check: Should NOT exist as student yet
        boolean existsAsStudentBefore = studentRepository.existsById(userId);
        assertThat(existsAsStudentBefore).isFalse();

        // Act: Execute native insert
        studentRepository.insertStudent(userId);

        // Force clear cache to fetch real data from DB
        entityManager.clear();

        // Assert
        boolean existsAsStudentAfter = studentRepository.existsById(userId);
        assertThat(existsAsStudentAfter).isTrue();
    }
}