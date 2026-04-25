package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.UserDTO;
import com.acasado.opored.dto.UserUpdateRequest;
import com.acasado.opored.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserService userService;

    @Test
    void When_GetAllUsers_Expect_OkAndList() throws Exception {
        // Arrange
        UserDTO user = UserDTO.builder().id(1).name("John").surname("Doe").alias("jdoe").email("john@example.com").build();
        when(userService.getAllUsers()).thenReturn(List.of(user));

        // Act
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].alias").value("jdoe"));
    }

    @Test
    void When_GetMe_Expect_OkAndUser() throws Exception {
        // Arrange
        UserDTO user = UserDTO.builder().id(1).alias("jdoe").build();
        when(userService.getMe()).thenReturn(user);

        // Act
        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alias").value("jdoe"));
    }

    @Test
    void When_UpdateMe_Expect_OkAndUpdatedUser() throws Exception {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest("John", "Doe", "jdoe", "NewPass123!", "photo.png");
        UserDTO updatedUser = UserDTO.builder().id(1).alias("jdoe").build();
        when(userService.updateMe(any(UserUpdateRequest.class))).thenReturn(updatedUser);

        // Act
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alias").value("jdoe"));
    }

    @Test
    void When_DisableUser_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(userService).disableUser(anyInt());

        // Act
        mockMvc.perform(put("/api/users/disable/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(userService).disableUser(1);
    }

    @Test
    void When_EnableUser_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(userService).enableUser(anyInt());

        // Act
        mockMvc.perform(put("/api/users/enable/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(userService).enableUser(1);
    }

    @Test
    void When_DeleteMe_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteMe();

        // Act
        mockMvc.perform(delete("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(userService).deleteMe();
    }
}
