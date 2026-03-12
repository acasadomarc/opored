package com.acasado.opored.integration.base;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected ProfessorRepository professorRepository;

    @Autowired
    protected ModeratorRepository moderatorRepository;

    protected void authenticateAs(Integer userId, String... authorities) {
        Collection<? extends GrantedAuthority> grantedAuthorities = authorities.length == 0
                ? Collections.emptyList()
                : Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    protected RoleEntity createRole(RoleEnum roleEnum) {
        RoleEntity role = new RoleEntity();
        role.setName(roleEnum);
        role.setPermissions(Collections.emptySet());
        return roleRepository.save(role);
    }

    protected StudentEntity createStudent(String email) {
        RoleEntity role = createRole(RoleEnum.STUDENT);
        StudentEntity student = new StudentEntity("Student", "User","alias", email, "password",
                new UserAccountStatus(true, true, true, true),
                role);
        return studentRepository.save(student);
    }

    protected ProfessorEntity createProfessor(String email) {
        RoleEntity role = createRole(RoleEnum.PROFESSOR);
        ProfessorEntity professor = new ProfessorEntity("Professor", "User","alias", email, "password",
                new UserAccountStatus(true, true, true, true),
                role,
                Collections.emptySet());
        return professorRepository.save(professor);
    }

    protected ModeratorEntity createModerator(String email) {
        RoleEntity role = createRole(RoleEnum.MODERATOR);
        ModeratorEntity moderator = new ModeratorEntity("Moderator", "User","alias", email, "password",
                new UserAccountStatus(true, true, true, true),
                role);
        return moderatorRepository.save(moderator);
    }
}