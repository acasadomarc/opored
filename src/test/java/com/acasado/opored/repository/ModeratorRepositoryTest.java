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
    private TestEntityManager entityManager;

    private RoleEntity moderatorRole;

    @BeforeEach
    void setUp() {
        // Persist roles needed for foreign Keys
        moderatorRole = new RoleEntity();
        moderatorRole.setName(RoleEnum.MODERATOR);
        moderatorRole.setPermissions(new HashSet<>());
        entityManager.persist(moderatorRole);

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
}