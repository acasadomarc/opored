package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.ModeratorDTO;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.service.ModeratorService;
import com.acasado.opored.util.ModeratorFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModeratorController.class)
@AutoConfigureMockMvc(addFilters = false) // Security disabled for unit tests
class ModeratorControllerTest extends BaseControllerTest {

    @MockitoBean
    private ModeratorService moderatorService;

    @Test
    void When_GetAllModerators_Expect_OkAndList() throws Exception {
        // Arrange
        List<ModeratorDTO> dtoList = List.of(ModeratorFactory.createValidModeratorDTO());
        when(moderatorService.getAllModerators()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/moderators")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("mod@example.com"));
    }

    @Test
    void When_GetModeratorById_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModeratorDTO dto = ModeratorFactory.createValidModeratorDTO();
        when(moderatorService.getModeratorById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/moderators/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetModeratorById_DoesNotExist() throws Exception {
        // Arrange
        when(moderatorService.getModeratorById(anyInt()))
                .thenThrow(new EntityNotFoundException("Moderator not found"));

        // Act
        mockMvc.perform(get("/api/moderators/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Moderator not found\"}"));
    }

    @Test
    void When_GetModeratorByEmail_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModeratorDTO dto = ModeratorFactory.createValidModeratorDTO();
        when(moderatorService.getModeratorByEmail(anyString())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/moderators/email/{email}", "mod@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void When_CreateModerator_Expect_CreatedAndAuthResponse() throws Exception {
        // Arrange
        ModeratorDTO inputDto = ModeratorFactory.createValidModeratorDTO();
        AuthResponse authResponse = ModeratorFactory.createAuthResponse();
        when(moderatorService.createModerator(any(ModeratorDTO.class))).thenReturn(authResponse);

        // Act
        mockMvc.perform(post("/api/moderators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()));
    }

    @Test
    void Expect_BadRequest_When_CreateModerator_InvalidData() throws Exception {
        // Arrange
        ModeratorDTO invalidDto = ModeratorFactory.createInvalidModeratorDTO();

        // Act
        mockMvc.perform(post("/api/moderators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(moderatorService, never()).createModerator(any());
    }
}