package com.acasado.opored.config;

import com.acasado.opored.config.filter.JwtTokenValidator;
import com.acasado.opored.util.JwtUtils;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenValidatorTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtTokenValidator jwtTokenValidator;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void When_DoFilterInternal_WithValidToken_Expect_SecurityContextSet() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer valid.jwt.token";
        String extractedToken = "valid.jwt.token";
        String userEmail = "test@example.com";

        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);
        Claim claimMock = mock(Claim.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validToken);
        when(jwtUtils.validateToken(extractedToken)).thenReturn(decodedJWTMock);
        when(jwtUtils.extractUser(decodedJWTMock)).thenReturn(userEmail);
        when(jwtUtils.getSpecificClaim(decodedJWTMock, "authorities")).thenReturn(claimMock);
        when(claimMock.asString()).thenReturn("ROLE_STUDENT,READ_PRIVILEGE");

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userEmail, auth.getPrincipal());
        assertEquals(2, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void When_DoFilterInternal_WithValidTokenButNoAuthorities_Expect_EmptyAuthoritiesList() throws ServletException, IOException {
        // Arrange
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);
        Claim claimMock = mock(Claim.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid.jwt.token");
        when(jwtUtils.validateToken(anyString())).thenReturn(decodedJWTMock);
        when(jwtUtils.extractUser(decodedJWTMock)).thenReturn("test@example.com");
        when(jwtUtils.getSpecificClaim(decodedJWTMock, "authorities")).thenReturn(claimMock);
        when(claimMock.asString()).thenReturn(null); // Simulamos que no tiene roles asignados

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().isEmpty());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void When_DoFilterInternal_WithoutHeader_Expect_ChainContinuesUnauthenticated() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void When_DoFilterInternal_WithInvalidPrefix_Expect_ChainContinuesUnauthenticated() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic SomeBase64String");

        // Act
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }
}