package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.CourseDTO;
import com.acasado.opored.service.CourseService;
import com.acasado.opored.util.CourseFactory;
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

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest extends BaseControllerTest {

    @MockitoBean
    private CourseService courseService;

    @Test
    void When_GetAllCourses_Expect_OkAndList() throws Exception {
        // Arrange
        List<CourseDTO> dtoList = List.of(CourseFactory.createValidCourseDTO());
        when(courseService.getAllCourses()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetCourseById_Expect_OkAndDTO() throws Exception {
        // Arrange
        CourseDTO dto = CourseFactory.createValidCourseDTO();
        when(courseService.getCourseById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/courses/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void When_CreateCourse_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        CourseDTO inputDto = CourseFactory.createValidCourseDTO();
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(inputDto.getName()));
    }

    @Test
    void When_UpdateMyCourse_Expect_OkAndDTO() throws Exception {
        // Arrange
        CourseDTO updatedDto = CourseFactory.createValidCourseDTO();
        updatedDto.setName("Updated Name");

        when(courseService.updateCourse(anyInt(), anyString(), anyString(), anyFloat()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/courses/me/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void When_AddDiscount_Expect_OkAndNewPrice() throws Exception {
        // Arrange
        Float expectedPrice = 80.0f;
        when(courseService.addDiscount(anyInt(), anyFloat())).thenReturn(expectedPrice);

        // Act
        mockMvc.perform(put("/api/courses/id/{id}/discount", 1)
                        .param("discountPercentage", "0.2")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("80.0"));
    }

    @Test
    void When_DeleteCourse_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(courseService).deleteCourse(anyInt());

        // Act
        mockMvc.perform(delete("/api/courses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());
    }
}