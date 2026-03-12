package com.acasado.opored.jpa;

import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.exception.EmailAlreadyRegisteredException;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.*;
import com.acasado.opored.security.BruteForceSecurityService;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.service.jpa.RefreshTokenService;
import com.acasado.opored.util.AuthFactory;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private BruteForceSecurityService bruteForceSecurityService;
    @Mock private JwtUtils jwtUtils;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private JpaUserDetailsService userDetailsService;

    // --- LoadUserByUsername Tests ---

    @Test
    void When_LoadUserByUsername_Expect_UserDetails() {
        // Arrange
        UserEntity user = AuthFactory.createUserEntity(RoleEnum.ADMIN);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getUsername());
    }

    @Test
    void Expect_UsernameNotFoundException_When_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown"));
    }

    // --- CreateUser Tests ---

    @Test
    void When_CreateUser_Student_Expect_Success() {
        // Arrange
        AuthCreateUserRequest request = AuthFactory.createValidRegisterRequest(RoleEnum.STUDENT);
        RoleEntity role = AuthFactory.createRoleEntity(RoleEnum.STUDENT);
        RefreshTokenEntity refreshTokenEntity = AuthFactory.createRefreshTokenEntity();
        StudentEntity savedStudent = new StudentEntity();
        savedStudent.setId(1);
        savedStudent.setRole(role);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.getRoleByName(RoleEnum.STUDENT)).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(studentRepository.save(any(StudentEntity.class))).thenReturn(savedStudent);
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("token");
        when(refreshTokenService.createRefreshToken(savedStudent.getId())).thenReturn(refreshTokenEntity);

        // Act
        AuthResponse response = userDetailsService.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getEmail(), response.getUsername());
        verify(studentRepository).save(any(StudentEntity.class));
    }

    @Test
    void Expect_EmailAlreadyRegisteredException_When_EmailExists() {
        // Arrange
        AuthCreateUserRequest request = AuthFactory.createValidRegisterRequest(RoleEnum.STUDENT);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new StudentEntity()));

        // Act & Assert
        assertThrows(EmailAlreadyRegisteredException.class, () -> userDetailsService.createUser(request));
    }

    @Test
    void Expect_BadCredentialsException_When_PasswordTooWeak() {
        // Arrange
        AuthCreateUserRequest request = AuthFactory.createInvalidPasswordRegisterRequest(RoleEnum.STUDENT);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userDetailsService.createUser(request));
    }

    // --- LoginUser Tests ---

    @Test
    void When_LoginUser_Success_Expect_Token() {
        // Arrange
        AuthLoginRequest loginRequest = AuthFactory.createValidLoginRequest();
        UserEntity userEntity = AuthFactory.createUserEntity(RoleEnum.ADMIN);
        RefreshTokenEntity refreshTokenEntity = AuthFactory.createRefreshTokenEntity();


        when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(bruteForceSecurityService.isBlocked(anyString())).thenReturn(false);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(userEntity.getId())).thenReturn(refreshTokenEntity);

        // Mock SecurityContext
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
    void Expect_BadCredentials_When_Login_BlockedUser() {
        // Arrange
        AuthLoginRequest loginRequest = AuthFactory.createValidLoginRequest();
        UserEntity userEntity = AuthFactory.createUserEntity(RoleEnum.ADMIN);

        when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(bruteForceSecurityService.isBlocked(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userDetailsService.loginUser(loginRequest));
        verify(bruteForceSecurityService, never()).loginFailed(anyString());
    }

    @Test
    void Expect_BadCredentials_When_Login_WrongPassword() {
        // Arrange
        AuthLoginRequest loginRequest = AuthFactory.createValidLoginRequest();
        UserEntity userEntity = AuthFactory.createUserEntity(RoleEnum.ADMIN);

        when(userRepository.findByEmail(loginRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(bruteForceSecurityService.isBlocked(anyString())).thenReturn(false);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false); // Wrong password

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userDetailsService.loginUser(loginRequest));

        // Verify failure recording
        verify(bruteForceSecurityService).loginFailed(loginRequest.getUsername());
    }
}