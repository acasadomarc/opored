package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.RatingCourseDTO;
import com.acasado.opored.service.RatingCourseService;
import com.acasado.opored.util.RatingCourseFactory;
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

@WebMvcTest(RatingCourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingCourseControllerTest extends BaseControllerTest {

    @MockitoBean
    private RatingCourseService ratingCourseService;

    @Test
    void When_GetAllRatingCourses_Expect_OkAndList() throws Exception {
        // Arrange
        List<RatingCourseDTO> dtoList = List.of(RatingCourseFactory.createValidRatingCourseDTO());
        when(ratingCourseService.getAllRatingCourses()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/ratingCourses")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Great Course"));
    }

    @Test
    void When_GetRatingCourseById_Expect_OkAndDTO() throws Exception {
        // Arrange
        RatingCourseDTO dto = RatingCourseFactory.createValidRatingCourseDTO();
        when(ratingCourseService.getRatingCourseById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/ratingCourses/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetRatingCourseById_DoesNotExist() throws Exception {
        // Arrange
        when(ratingCourseService.getRatingCourseById(anyInt()))
                .thenThrow(new EntityNotFoundException("Rating not found"));

        // Act
        mockMvc.perform(get("/api/ratingCourses/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateRatingCourse_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        RatingCourseDTO inputDto = RatingCourseFactory.createValidRatingCourseDTO();
        when(ratingCourseService.createRatingCourse(any(RatingCourseDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/ratingCourses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(inputDto.getScore()));
    }

    @Test
    void Expect_BadRequest_When_CreateRatingCourse_InvalidData() throws Exception {
        // Arrange
        RatingCourseDTO invalidDto = RatingCourseFactory.createInvalidRatingCourseDTO();

        // Act
        mockMvc.perform(post("/api/ratingCourses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void When_UpdateMyRatingCourse_Expect_OkAndDTO() throws Exception {
        // Arrange
        RatingCourseDTO updatedDto = RatingCourseFactory.createValidRatingCourseDTO();
        updatedDto.setTitle("Updated Title");

        when(ratingCourseService.updateMyRatingCourse(anyInt(), anyString(), anyFloat(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/ratingCourses/me/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteRatingCourse_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(ratingCourseService).deleteRatingCourse(anyInt());

        // Act
        mockMvc.perform(delete("/api/ratingCourses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(ratingCourseService).deleteRatingCourse(1);
    }
}