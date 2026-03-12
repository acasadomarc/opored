package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.PublicExaminationDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.service.PublicExaminationService;
import com.acasado.opored.util.PublicExaminationFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicExaminationController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicExaminationControllerTest extends BaseControllerTest {

    @MockitoBean
    private PublicExaminationService publicExaminationService;

    @Test
    void When_GetAllPublicExaminations_Expect_OkAndList() throws Exception {
        // Arrange
        List<PublicExaminationDTO> dtoList = List.of(PublicExaminationFactory.createValidPublicExaminationDTO());
        when(publicExaminationService.getAllPublicExaminations()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/publicExaminations")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("National Police"));
    }

    @Test
    void When_GetPublicExaminationById_Expect_OkAndDTO() throws Exception {
        // Arrange
        PublicExaminationDTO dto = PublicExaminationFactory.createValidPublicExaminationDTO();
        when(publicExaminationService.getPublicExaminationById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/publicExaminations/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetPublicExaminationById_DoesNotExist() throws Exception {
        // Arrange
        when(publicExaminationService.getPublicExaminationById(anyInt()))
                .thenThrow(new EntityNotFoundException("Public Examination not found"));

        // Act
        mockMvc.perform(get("/api/publicExaminations/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreatePublicExamination_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        PublicExaminationDTO inputDto = PublicExaminationFactory.createValidPublicExaminationDTO();
        when(publicExaminationService.createPublicExamination(any(PublicExaminationDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/publicExaminations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(inputDto.getCategoryId()));
    }

    @Test
    void Expect_BadRequest_When_CreatePublicExamination_InvalidData() throws Exception {
        // Arrange
        PublicExaminationDTO invalidDto = PublicExaminationFactory.createInvalidPublicExaminationDTO();

        // Act
        mockMvc.perform(post("/api/publicExaminations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void When_UpdatePublicExamination_Expect_OkAndDTO() throws Exception {
        // Arrange
        PublicExaminationDTO updatedDto = PublicExaminationFactory.createValidPublicExaminationDTO();
        updatedDto.setName("Updated Name");

        when(publicExaminationService.updatePublicExamination(anyInt(), anyString(), anyString(), anyInt()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/publicExaminations/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void When_DeletePublicExamination_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(publicExaminationService).deletePublicExamination(anyInt());

        // Act
        mockMvc.perform(delete("/api/publicExaminations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(publicExaminationService).deletePublicExamination(1);
    }

    @Test
    void When_GetStudents_Expect_OkAndList() throws Exception {
        // Arrange
        Set<StudentSummaryDTO> students = Set.of(new StudentSummaryDTO());
        when(publicExaminationService.getStudents(anyInt())).thenReturn(students);

        // Act
        mockMvc.perform(get("/api/publicExaminations/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}