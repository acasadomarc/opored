package com.acasado.opored.jpa;

import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.dto.auth.RefreshData;
import com.acasado.opored.enumeration.PermissionEnum;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.AliasAlreadyRegisteredException;
import com.acasado.opored.exception.EmailAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.security.BruteForceSecurityService;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.security.RefreshTokenService;
import com.acasado.opored.security.SecurityUtils;
import com.acasado.opored.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AdministratorRepository administratorRepository;
    @Mock private ModeratorRepository moderatorRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private BruteForceSecurityService bruteForceSecurityService;
    @Mock private JwtUtils jwtUtils;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private JpaUserDetailsService userDetailsService;

    @Test
    void When_LoadUserByUsername_Expect_UserDetailsWithPermissions() {
        // Arrange
        UserEntity user = createMockUser(RoleEnum.ADMIN);
        PermissionEntity permission = new PermissionEntity();
        permission.setName(PermissionEnum.ADMINISTRATION_CREATE);
        user.getRole().setPermissions(Set.of(permission));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getUsername());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMINISTRATION_CREATE")));
    }

    @Test
    void Expect_UsernameNotFoundException_When_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown@example.com"));
    }

    @Test
    void When_CreatePrivilegedUser_Admin_Expect_SuccessNoTokens() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("ADMIN");
        RoleEntity role = createMockRole(RoleEnum.ADMIN);

        setupValidationMocksForSuccess();
        when(roleRepository.getRoleByName(RoleEnum.ADMIN)).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            setupSecurityUtilsValidationMocks(utilities);

            // Act
            AuthResponse response = userDetailsService.createPrivilegedUser(request);

            // Assert
            assertNotNull(response);
            assertEquals(request.getEmail(), response.getUsername());
            assertNull(response.getAccessToken()); // Privileged users don't return tokens on creation
            verify(administratorRepository).save(any(AdministratorEntity.class));
        }
    }

    @Test
    void When_CreatePrivilegedUser_Moderator_Expect_SuccessNoTokens() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("MODERATOR");
        RoleEntity role = createMockRole(RoleEnum.MODERATOR);

        setupValidationMocksForSuccess();
        when(roleRepository.getRoleByName(RoleEnum.MODERATOR)).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            setupSecurityUtilsValidationMocks(utilities);

            // Act
            AuthResponse response = userDetailsService.createPrivilegedUser(request);

            // Assert
            assertNotNull(response);
            verify(moderatorRepository).save(any(ModeratorEntity.class));
        }
    }

    @Test
    void Expect_BadCredentialsException_When_CreatePrivilegedUser_InvalidRole() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("STUDENT"); // Invalid for privileged

        setupValidationMocksForSuccess();

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            setupSecurityUtilsValidationMocks(utilities);

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> userDetailsService.createPrivilegedUser(request));
        }
    }

    @Test
    void When_CreatePublicUser_Professor_Expect_SuccessAndTokens() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("PROFESSOR");
        RoleEntity role = createMockRole(RoleEnum.PROFESSOR);
        ProfessorEntity savedProfessor = new ProfessorEntity();
        savedProfessor.setId(1);
        savedProfessor.setRole(role);

        setupValidationMocksForSuccess();
        when(roleRepository.getRoleByName(RoleEnum.PROFESSOR)).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(professorRepository.save(any(ProfessorEntity.class))).thenReturn(savedProfessor);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken("refresh-token");
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(savedProfessor.getId())).thenReturn(refreshTokenEntity);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            setupSecurityUtilsValidationMocks(utilities);

            // Act
            AuthResponse response = userDetailsService.createPublicUser(request);

            // Assert
            assertNotNull(response);
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            verify(professorRepository).save(any(ProfessorEntity.class));
        }
    }

    @Test
    void Expect_EmailAlreadyRegisteredException_When_FieldsValidation_EmailExists() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("STUDENT");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new StudentEntity()));

        // Act & Assert
        String email = request.getEmail();
        assertThrows(EmailAlreadyRegisteredException.class, () -> userDetailsService.fieldsValidation(request, email));
    }

    @Test
    void Expect_AliasAlreadyRegisteredException_When_FieldsValidation_AliasExists() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("STUDENT");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByAlias(request.getAlias())).thenReturn(Optional.of(new StudentEntity()));

        // Act & Assert
        String email = request.getEmail();
        assertThrows(AliasAlreadyRegisteredException.class, () -> userDetailsService.fieldsValidation(request, email));
    }

    @Test
    void Expect_BadCredentialsException_When_FieldsValidation_InvalidEmail() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("STUDENT");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByAlias(request.getAlias())).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(() -> SecurityUtils.emailValidation(request.getEmail())).thenReturn(false);

            // Act & Assert
            String email = request.getEmail();
            assertThrows(BadCredentialsException.class, () -> userDetailsService.fieldsValidation(request, email));
        }
    }

    @Test
    void Expect_BadCredentialsException_When_FieldsValidation_InvalidPassword() {
        // Arrange
        AuthCreateUserRequest request = createMockRegisterRequest("STUDENT");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByAlias(request.getAlias())).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(() -> SecurityUtils.emailValidation(request.getEmail())).thenReturn(true);
            utilities.when(() -> SecurityUtils.passwordValidation(request.getPassword())).thenReturn(false);

            // Act & Assert
            String email = request.getEmail();
            assertThrows(BadCredentialsException.class, () -> userDetailsService.fieldsValidation(request, email));
        }
    }

    @Test
    void When_LoginUser_Expect_SuccessAndTokens() {
        // Arrange
        AuthLoginRequest loginRequest = new AuthLoginRequest("user@example.com", "password");
        UserEntity userEntity = createMockUser(RoleEnum.STUDENT);
        userEntity.setId(1);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken("refresh-token");

        when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(bruteForceSecurityService.isBlocked(anyString())).thenReturn(false);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(userEntity.getId())).thenReturn(refreshTokenEntity);

        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            AuthResponse response = userDetailsService.loginUser(loginRequest);

            // Assert
            assertNotNull(response);
            assertEquals("access-token", response.getAccessToken());
            verify(bruteForceSecurityService).loginSucceeded(loginRequest.getUsername());
            verify(securityContext).setAuthentication(any(Authentication.class));
        }
    }

    @Test
    void Expect_BadCredentialsException_When_Authenticate_UserNotFoundInRepository() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userDetailsService.authenticate("unknown@example.com", "pass"));
    }

    @Test
    void When_RefreshToken_Expect_NewTokens() {
        // Arrange
        RefreshData refreshData = new RefreshData("old-refresh-token");
        UserEntity user = createMockUser(RoleEnum.STUDENT);
        user.setId(1);

        RefreshTokenEntity oldRefreshEntity = new RefreshTokenEntity();
        oldRefreshEntity.setToken("old-refresh-token");
        oldRefreshEntity.setUser(user);

        RefreshTokenEntity newRefreshEntity = new RefreshTokenEntity();
        newRefreshEntity.setToken("new-refresh-token");

        when(refreshTokenService.verifyExpiration(refreshData.getRefreshToken())).thenReturn(oldRefreshEntity);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); // Para el loadUserByUsername interno
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("new-access-token");
        when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(newRefreshEntity);

        // Act
        AuthResponse response = userDetailsService.refreshToken(refreshData);

        // Assert
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(refreshTokenService).revoke(oldRefreshEntity);
    }

    @Test
    void When_Logout_Expect_TokenRevoked() {
        // Arrange
        RefreshData refreshData = new RefreshData("valid-refresh-token");
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

        when(refreshTokenService.verifyExpiration(refreshData.getRefreshToken())).thenReturn(refreshTokenEntity);

        // Act
        userDetailsService.logout(refreshData);

        // Assert
        verify(refreshTokenService).revoke(refreshTokenEntity);
    }

    // --- Helper Methods ---

    private UserEntity createMockUser(RoleEnum roleEnum) {
        UserEntity user = new StudentEntity(); // Can be any subclass
        user.setEmail("user@example.com");
        user.setPassword("encodedPass");
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setCredentialNoExpired(true);
        user.setAccountNoLocked(true);

        RoleEntity role = createMockRole(roleEnum);
        user.setRole(role);
        return user;
    }

    private RoleEntity createMockRole(RoleEnum roleEnum) {
        RoleEntity role = new RoleEntity();
        role.setName(roleEnum);
        role.setPermissions(new HashSet<>());
        return role;
    }

    private AuthCreateUserRequest createMockRegisterRequest(String role) {
        AuthCreateUserRequest request = new AuthCreateUserRequest();
        request.setName("Name");
        request.setSurname("Surname");
        request.setAlias("mockAlias");
        request.setEmail("newuser@example.com");
        request.setPassword("ValidPass1@");
        request.setRole(role);
        return request;
    }

    private void setupValidationMocksForSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByAlias(anyString())).thenReturn(Optional.empty());
    }

    private void setupSecurityUtilsValidationMocks(MockedStatic<SecurityUtils> utilities) {
        utilities.when(() -> SecurityUtils.emailValidation(anyString())).thenReturn(true);
        utilities.when(() -> SecurityUtils.passwordValidation(anyString())).thenReturn(true);
        utilities.when(() -> SecurityUtils.publicUserAliasValidation(anyString())).thenReturn(true);
        utilities.when(() -> SecurityUtils.privilegedUserAliasValidation(anyString())).thenReturn(true);
    }
}