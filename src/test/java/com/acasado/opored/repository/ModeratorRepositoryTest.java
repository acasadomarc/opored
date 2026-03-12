package com.acasado.opored.repository;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
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
class ModeratorRepositoryTest {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private RoleEntity moderatorRole;
    private RoleEntity studentRole;

    @BeforeEach
    void setUp() {
        // Persist roles needed for foreign Keys
        moderatorRole = new RoleEntity();
        moderatorRole.setName(RoleEnum.MODERATOR);
        moderatorRole.setPermissions(new HashSet<>());
        entityManager.persist(moderatorRole);

        studentRole = new RoleEntity();
        studentRole.setName(RoleEnum.STUDENT);
        studentRole.setPermissions(new HashSet<>());
        entityManager.persist(studentRole);

        entityManager.flush();
    }

    @Test
    void whenFindByEmail_thenReturnModerator() {
        // Arrange
        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setName("Mod");
        moderator.setSurname("Erator");
        moderator.setAlias("alias");
        moderator.setEmail("mod@test.com");
        moderator.setPassword("pass");
        moderator.setRegistrationDate(LocalDate.now());
        moderator.setRole(moderatorRole);
        moderator.setEnabled(true);
        moderator.setIsDeleted(false);
        moderator.setAccountNoExpired(true);
        moderator.setAccountNoLocked(true);
        moderator.setCredentialNoExpired(true);

        entityManager.persist(moderator);
        entityManager.flush();

        // Act
        Optional<ModeratorEntity> found = moderatorRepository.findByEmail("mod@test.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("mod@test.com");
    }

    @Test
    void whenInsertModerator_thenRowExistsInModeratorsTable() {
        // Arrange: Create a Student first (simulating a user that exists but isn't a moderator yet)
        StudentEntity student = new StudentEntity();
        student.setName("Student");
        student.setSurname("ToPromote");
        student.setAlias("alias");
        student.setEmail("promote@test.com");
        student.setPassword("pass");
        student.setRegistrationDate(LocalDate.now());
        student.setRole(studentRole);
        student.setEnabled(true);
        student.setIsDeleted(false);
        student.setAccountNoExpired(true);
        student.setAccountNoLocked(true);
        student.setCredentialNoExpired(true);

        StudentEntity savedStudent = entityManager.persist(student);
        entityManager.flush();
        Integer userId = savedStudent.getId();

        // Act: Run the native query to "Promote"
        // Note: This only inserts into the 'moderators' table using the existing ID.
        // It does NOT change the discriminatory column or update the 'users' table logic in Java,
        // that logic happens in the Service. Here we test the SQL execution.
        moderatorRepository.insertModerator(userId);

        // Clear persistence context to force fetching fresh data from DB
        entityManager.clear();

        // Assert
        // We check if we can verify the ID exists in the repository now
        boolean exists = moderatorRepository.existsById(userId);
        assertThat(exists).isTrue();
    }

    @Test
    void whenDeleteModerator_thenUserRemainsActive() {
        // Arrange: Create a Moderator
        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setName("Demote");
        moderator.setSurname("Me");
        moderator.setAlias("alias");
        moderator.setEmail("demote@test.com");
        moderator.setPassword("pass");
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

        // Act: Demote (Delete from moderators table only)
        moderatorRepository.deleteModerator(userId);
        entityManager.clear();

        // Assert
        // Should NOT exist in Moderator repository anymore
        boolean isModerator = moderatorRepository.existsById(userId);
        assertThat(isModerator).isFalse();

        // Should STILL exist in User repository
        boolean isUser = userRepository.existsById(userId);
        assertThat(isUser).isTrue();
    }
}