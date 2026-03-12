package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import com.acasado.opored.util.AuthFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest extends BaseControllerTest {

    @MockitoBean
    private JpaUserDetailsService userDetailsService;

    @Test
    void When_Login_Expect_OkAndAuthResponse() throws Exception {
        // Arrange
        AuthLoginRequest request = AuthFactory.createValidLoginRequest();
        AuthResponse response = AuthFactory.createAuthResponse();

        when(userDetailsService.loginUser(any(AuthLoginRequest.class))).thenReturn(response);

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("user@example.com"));
    }

    @Test
    void Expect_BadRequest_When_Login_InvalidBody() throws Exception {
        // Arrange
        AuthLoginRequest invalidRequest = AuthFactory.createInvalidLoginRequest();

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void Expect_Unauthorized_When_Login_BadCredentials() throws Exception {
        // Arrange
        AuthLoginRequest request = AuthFactory.createValidLoginRequest();
        when(userDetailsService.loginUser(any(AuthLoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isUnauthorized());
    }
}