package com.acasado.opored.repository;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.AdministratorEntity;
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
class AdministratorRepositoryTest {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private RoleEntity adminRole;
    private RoleEntity studentRole;

    @BeforeEach
    void setUp() {
        // Persist Roles required for User creation
        adminRole = new RoleEntity();
        adminRole.setName(RoleEnum.ADMIN);
        adminRole.setPermissions(new HashSet<>());
        entityManager.persist(adminRole);

        studentRole = new RoleEntity();
        studentRole.setName(RoleEnum.STUDENT);
        studentRole.setPermissions(new HashSet<>());
        entityManager.persist(studentRole);

        entityManager.flush();
    }

    @Test
    void whenFindByEmail_thenReturnAdministrator() {
        // Arrange
        AdministratorEntity admin = new AdministratorEntity();
        admin.setName("Admin");
        admin.setSurname("User");
        admin.setAlias("alias");
        admin.setEmail("admin@test.com");
        admin.setPassword("securepass");
        admin.setRegistrationDate(LocalDate.now());
        admin.setRole(adminRole);
        admin.setEnabled(true);
        admin.setIsDeleted(false);
        admin.setAccountNoExpired(true);
        admin.setAccountNoLocked(true);
        admin.setCredentialNoExpired(true);

        entityManager.persist(admin);
        entityManager.flush();

        // Act
        Optional<AdministratorEntity> found = administratorRepository.findByEmail("admin@test.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    void whenInsertAdministrator_thenRowExistsInAdministratorsTable() {
        // Arrange: Create a Student first (simulating a generic user to be promoted)
        StudentEntity student = new StudentEntity();
        student.setName("Future");
        student.setSurname("Admin");
        student.setAlias("alias");
        student.setEmail("future.admin@test.com");
        student.setPassword("1234");
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

        // Act: Execute native insert
        administratorRepository.insertAdministrator(userId);

        // Force clear cache to fetch real data from DB
        entityManager.clear();

        // Assert
        // Check if the ID now exists in the Administrator repository context
        boolean existsAsAdmin = administratorRepository.existsById(userId);
        assertThat(existsAsAdmin).isTrue();
    }

    @Test
    void whenDeleteAdministrator_thenUserRemainsActive() {
        // Arrange: Create an Administrator
        AdministratorEntity admin = new AdministratorEntity();
        admin.setName("To");
        admin.setSurname("Delete");
        admin.setAlias("alias");
        admin.setEmail("delete.admin@test.com");
        admin.setPassword("1234");
        admin.setRegistrationDate(LocalDate.now());
        admin.setRole(adminRole);
        admin.setEnabled(true);
        admin.setIsDeleted(false);
        admin.setAccountNoExpired(true);
        admin.setAccountNoLocked(true);
        admin.setCredentialNoExpired(true);

        AdministratorEntity savedAdmin = entityManager.persist(admin);
        entityManager.flush();
        Integer userId = savedAdmin.getId();

        // Act: Execute native delete
        administratorRepository.deleteAdministrator(userId);

        // Force clear cache
        entityManager.clear();

        // Assert
        // Should NOT exist as Administrator
        boolean isAdministrator = administratorRepository.existsById(userId);
        assertThat(isAdministrator).isFalse();

        //  Should STILL exist as a User
        boolean isUser = userRepository.existsById(userId);
        assertThat(isUser).isTrue();
    }
}