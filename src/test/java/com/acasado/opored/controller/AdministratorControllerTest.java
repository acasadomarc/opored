package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.AdministratorDTO;
import com.acasado.opored.dto.auth.AuthResponse;
import com.acasado.opored.service.AdministratorService;
import com.acasado.opored.util.AdministratorFactory;
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

@WebMvcTest(AdministratorController.class)
@AutoConfigureMockMvc(addFilters = false) // Security disabled for unit tests
class AdministratorControllerTest extends BaseControllerTest {

    @MockitoBean
    private AdministratorService administratorService;

    @Test
    void When_GetAllAdministrators_Expect_OkAndList() throws Exception {
        // Arrange
        List<AdministratorDTO> dtoList = List.of(AdministratorFactory.createValidAdministratorDTO());
        when(administratorService.getAllAdministrators()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/administrators")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void When_GetAdministratorById_Expect_OkAndDTO() throws Exception {
        // Arrange
        AdministratorDTO dto = AdministratorFactory.createValidAdministratorDTO();
        when(administratorService.getAdministratorById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/administrators/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void Expect_NotFound_When_GetAdministratorById_DoesNotExist() throws Exception {
        // Arrange
        when(administratorService.getAdministratorById(anyInt()))
                .thenThrow(new EntityNotFoundException("Administrator not found"));

        // Act
        mockMvc.perform(get("/api/administrators/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Administrator not found\"}"));
    }

    @Test
    void When_GetAdministratorByEmail_Expect_OkAndDTO() throws Exception {
        // Arrange
        AdministratorDTO dto = AdministratorFactory.createValidAdministratorDTO();
        when(administratorService.getAdministratorByEmail(anyString())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/administrators/email/{email}", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void When_CreateAdministrator_Expect_CreatedAndAuthResponse() throws Exception {
        // Arrange
        AdministratorDTO inputDto = AdministratorFactory.createValidAdministratorDTO();
        AuthResponse authResponse = AdministratorFactory.createAuthResponse();

        when(administratorService.createAdministrator(any(AdministratorDTO.class)))
                .thenReturn(authResponse);

        // Act
        mockMvc.perform(post("/api/administrators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()))
                .andExpect(jsonPath("$.username").value(authResponse.getUsername()))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void Expect_BadRequest_When_CreateAdministrator_InvalidData() throws Exception {
        // Arrange
        AdministratorDTO invalidDto = AdministratorFactory.createInvalidAdministratorDTO();

        // Act
        mockMvc.perform(post("/api/administrators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(administratorService, never()).createAdministrator(any());
    }

    @Test
    void When_DeleteAdministrator_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(administratorService).deleteAdministrator(anyInt());

        // Act
        mockMvc.perform(delete("/api/administrators/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(administratorService, times(1)).deleteAdministrator(1);
    }
}