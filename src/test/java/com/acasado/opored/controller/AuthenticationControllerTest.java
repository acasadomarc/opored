package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.auth.AuthCreateUserRequest;
import com.acasado.opored.dto.auth.AuthLoginRequest;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.dto.auth.RefreshData;
import com.acasado.opored.service.jpa.JpaUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest extends BaseControllerTest {

    @MockitoBean
    private JpaUserDetailsService userDetailsService;

    @Test
    void When_SignUp_Expect_Created() throws Exception {
        AuthCreateUserRequest request = new AuthCreateUserRequest("Name", "Surname", "alias", "email@test.com", "Pass123!@#456", "STUDENT");
        AuthResponse response = new AuthResponse("email@test.com", "User created", "access", "refresh", 201);

        when(userDetailsService.createPublicUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("email@test.com"));
    }

    @Test
    void When_CreateUserPrivileged_Expect_Created() throws Exception {
        AuthCreateUserRequest request = new AuthCreateUserRequest("Name", "Surname", "alias", "email@test.com", "Pass123!@#456", "ADMIN");
        AuthResponse response = new AuthResponse("email@test.com", "User created", null, null, 201);

        when(userDetailsService.createPrivilegedUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void When_Refresh_Expect_Ok() throws Exception {
        RefreshData data = new RefreshData("old-refresh");
        AuthResponse response = new AuthResponse("user", "Refreshed", "new-access", "new-refresh", 200);

        when(userDetailsService.refreshToken(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    void When_Logout_Expect_NoContent() throws Exception {
        RefreshData data = new RefreshData("refresh-token");
        doNothing().when(userDetailsService).logout(any());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNoContent());

        verify(userDetailsService).logout(any());
    }

    @Test
    void When_Login_Expect_Ok() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("user", "pass");
        AuthResponse response = new AuthResponse("user", "Logged in", "access", "refresh", 200);

        when(userDetailsService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
