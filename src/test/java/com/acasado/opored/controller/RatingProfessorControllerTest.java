package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.RatingProfessorDTO;
import com.acasado.opored.service.RatingProfessorService;
import com.acasado.opored.util.RatingProfessorFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingProfessorController.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingProfessorControllerTest extends BaseControllerTest {

    @MockitoBean
    private RatingProfessorService ratingProfessorService;

    @Test
    void When_GetAllRatingProfessors_Expect_OkAndList() throws Exception {
        // Arrange
        List<RatingProfessorDTO> dtoList = List.of(RatingProfessorFactory.createValidRatingProfessorDTO());
        when(ratingProfessorService.getAllRatingProfessors()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/ratingProfessors")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Great Professor"));
    }

    @Test
    void When_GetRatingProfessorById_Expect_OkAndDTO() throws Exception {
        // Arrange
        RatingProfessorDTO dto = RatingProfessorFactory.createValidRatingProfessorDTO();
        when(ratingProfessorService.getRatingProfessorById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/ratingProfessors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetRatingProfessorById_DoesNotExist() throws Exception {
        // Arrange
        when(ratingProfessorService.getRatingProfessorById(anyInt()))
                .thenThrow(new EntityNotFoundException("Rating not found"));

        // Act
        mockMvc.perform(get("/api/ratingProfessors/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateRatingProfessor_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        RatingProfessorDTO inputDto = RatingProfessorFactory.createValidRatingProfessorDTO();
        when(ratingProfessorService.createRatingProfessor(any(RatingProfessorDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/ratingProfessors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(inputDto.getScore()));
    }

    @Test
    void Expect_BadRequest_When_CreateRatingProfessor_InvalidData() throws Exception {
        // Arrange
        RatingProfessorDTO invalidDto = RatingProfessorFactory.createInvalidRatingProfessorDTO();

        // Act
        mockMvc.perform(post("/api/ratingProfessors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void When_UpdateMyRatingProfessor_Expect_OkAndDTO() throws Exception {
        // Arrange
        RatingProfessorDTO updatedDto = RatingProfessorFactory.createValidRatingProfessorDTO();
        updatedDto.setTitle("Updated Title");

        when(ratingProfessorService.updateMyRatingProfessor(anyInt(), anyString(), anyFloat(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/ratingProfessors/me/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteRatingProfessor_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(ratingProfessorService).deleteRatingProfessor(anyInt());

        // Act
        mockMvc.perform(delete("/api/ratingProfessors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(ratingProfessorService).deleteRatingProfessor(1);
    }
}