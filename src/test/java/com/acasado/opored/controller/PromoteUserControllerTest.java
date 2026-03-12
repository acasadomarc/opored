package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.service.PromoteUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromoteUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromoteUserControllerTest extends BaseControllerTest {

    @MockitoBean
    private PromoteUserService promoteUserService;

    @Test
    void When_PromoteToModerator_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(promoteUserService).promoteToModerator(anyInt());

        // Act
        mockMvc.perform(put("/api/promotions/promoteModerator/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated from student to moderator successfully"));

        verify(promoteUserService).promoteToModerator(1);
    }

    @Test
    void Expect_NotFound_When_PromoteToModerator_UserNotExists() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("User not found"))
                .when(promoteUserService).promoteToModerator(anyInt());

        // Act
        mockMvc.perform(put("/api/promotions/promoteModerator/{id}", 999))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_DemoteFromModerator_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(promoteUserService).demoteFromModerator(anyInt());

        // Act
        mockMvc.perform(put("/api/promotions/demoteModerator/{id}", 1))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated from moderator to student successfully"));

        verify(promoteUserService).demoteFromModerator(1);
    }

    @Test
    void When_PromoteToAdministrator_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(promoteUserService).promoteToAdministrator(anyInt(), anyString());

        // Act
        mockMvc.perform(put("/api/promotions/promoteAdministrator/{id}/actualRole/{role}", 1, "STUDENT"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated from STUDENT to administrator successfully"));

        verify(promoteUserService).promoteToAdministrator(1, "STUDENT");
    }

    @Test
    void When_DemoteFromAdministrator_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(promoteUserService).demoteFromAdministrator(anyInt(), anyString());

        // Act
        mockMvc.perform(put("/api/promotions/demoteAdministrator/{id}/newRole/{role}", 1, "MODERATOR"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Role updated from administrator to MODERATOR successfully"));

        verify(promoteUserService).demoteFromAdministrator(1, "MODERATOR");
    }
}