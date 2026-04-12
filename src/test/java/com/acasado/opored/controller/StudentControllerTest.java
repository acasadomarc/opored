package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.*;
import com.acasado.opored.service.StudentService;
import com.acasado.opored.util.StudentFactory;
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

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false) // Security disabled for unit tests
class StudentControllerTest extends BaseControllerTest {

    @MockitoBean
    private StudentService studentService;

    @Test
    void When_GetAllStudents_Expect_OkAndList() throws Exception {
        // Arrange
        List<StudentSummaryDTO> dtoList = List.of(StudentFactory.createValidStudentSummaryDTO());
        when(studentService.getAllStudents()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetStudentById_Expect_OkAndDTO() throws Exception {
        // Arrange
        StudentSummaryDTO dto = StudentFactory.createValidStudentSummaryDTO();
        when(studentService.getStudentById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    @Test
    void When_GetMe_Expect_OkAndDTO() throws Exception {
        // Arrange
        StudentDTO dto = StudentFactory.createValidStudentDTO();
        when(studentService.getMe()).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/students/me")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void When_DeleteStudent_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(studentService).disableStudent(anyInt());

        // Act
        mockMvc.perform(delete("/api/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());
    }

    // --- Topic Relations ---

    @Test
    void When_GetFollowedTopics_Expect_OkAndSet() throws Exception {
        // Arrange
        Set<TopicDTO> topics = StudentFactory.createTopicDTOSet();
        when(studentService.getFollowedTopics()).thenReturn(topics);

        // Act
        mockMvc.perform(get("/api/students/me/topics")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(topics.size()));
    }

    @Test
    void When_FollowTopic_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(studentService).followTopic(anyInt());

        // Act
        mockMvc.perform(post("/api/students/me/topics/{topicId}", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    @Test
    void When_UnfollowTopic_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(studentService).unfollowTopic(anyInt());

        // Act
        mockMvc.perform(delete("/api/students/me/topics/{topicId}", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    // --- Public Examination Relations ---

    @Test
    void When_SignUpForPublicExamination_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(studentService).signUpForPublicExamination(anyInt());

        // Act
        mockMvc.perform(post("/api/students/me/publicExamination/{id}", 200)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    @Test
    void When_WithdrawFromPublicExamination_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(studentService).withdrawFromPublicExamination(anyInt());

        // Act
        mockMvc.perform(delete("/api/students/me/publicExamination/{id}", 200)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    // --- Course & Purchase Relations ---

    @Test
    void When_GetCourses_Expect_OkAndSet() throws Exception {
        // Arrange
        Set<CourseDTO> courses = StudentFactory.createCourseDTOSet();
        when(studentService.getCourses()).thenReturn(courses);

        // Act
        mockMvc.perform(get("/api/students/me/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }

    @Test
    void When_GetPurchases_Expect_OkAndSet() throws Exception {
        // Arrange
        Set<PurchaseDTO> purchases = StudentFactory.createPurchaseDTOSet();
        when(studentService.getPurchases()).thenReturn(purchases);

        // Act
        mockMvc.perform(get("/api/students/me/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());
    }
}