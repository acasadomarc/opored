package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.ProfessorDTO;
import com.acasado.opored.service.ProfessorService;
import com.acasado.opored.util.ProfessorFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessorController.class)
@AutoConfigureMockMvc(addFilters = false) // Security disabled for unit tests
class ProfessorControllerTest extends BaseControllerTest {

    @MockitoBean
    private ProfessorService professorService;

    @Test
    void When_GetAllProfessors_Expect_OkAndList() throws Exception {
        // Arrange
        List<ProfessorDTO> dtoList = List.of(ProfessorFactory.createValidProfessorDTO());
        when(professorService.getAllProfessors()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/professors")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("prof@example.com"));
    }

    @Test
    void When_GetProfessorById_Expect_OkAndDTO() throws Exception {
        // Arrange
        ProfessorDTO dto = ProfessorFactory.createValidProfessorDTO();
        when(professorService.getProfessorById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/professors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetProfessorById_DoesNotExist() throws Exception {
        // Arrange
        when(professorService.getProfessorById(anyInt()))
                .thenThrow(new EntityNotFoundException("Professor not found"));

        // Act
        mockMvc.perform(get("/api/professors/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_GetProfessorByEmail_Expect_OkAndDTO() throws Exception {
        // Arrange
        ProfessorDTO dto = ProfessorFactory.createValidProfessorDTO();
        when(professorService.getProfessorByEmail(anyString())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/professors/email/{email}", "prof@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void When_DeleteProfessor_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(professorService).disableProfessor(anyInt());

        // Act
        mockMvc.perform(delete("/api/professors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(professorService, times(1)).disableProfessor(1);
    }
}