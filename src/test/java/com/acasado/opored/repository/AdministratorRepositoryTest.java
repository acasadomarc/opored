package com.acasado.opored.repository;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.AdministratorEntity;
import com.acasado.opored.model.RoleEntity;
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
    private TestEntityManager entityManager;

    private RoleEntity adminRole;

    @BeforeEach
    void setUp() {
        // Persist Roles required for User creation
        adminRole = new RoleEntity();
        adminRole.setName(RoleEnum.ADMIN);
        adminRole.setPermissions(new HashSet<>());
        entityManager.persist(adminRole);

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
}