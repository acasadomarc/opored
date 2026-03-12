package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.service.QuizService;
import com.acasado.opored.util.QuizFactory;
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

@WebMvcTest(QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizControllerTest extends BaseControllerTest {

    @MockitoBean
    private QuizService quizService;

    @Test
    void When_GetAllQuizzes_Expect_OkAndList() throws Exception {
        // Arrange
        List<QuizDTO> dtoList = List.of(QuizFactory.createValidQuizDTO());
        when(quizService.getAllQuizzes()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Java Basics Exam"));
    }

    @Test
    void When_createQuiz_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        QuizDTO inputDto = QuizFactory.createValidQuizDTO();
        when(quizService.createQuiz(any(QuizDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(inputDto.getCourseId()));
    }

    @Test
    void Expect_BadRequest_When_createQuiz_InvalidData() throws Exception {
        // Arrange
        QuizDTO invalidDto = QuizFactory.createInvalidQuizDTO();

        // Act
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(quizService, never()).createQuiz(any());
    }

    @Test
    void When_UpdateMyTest_Expect_OkAndDTO() throws Exception {
        // Arrange
        QuizDTO inputDto = QuizFactory.createValidQuizDTO();
        inputDto.setTitle("Updated Title");
        when(quizService.updateQuiz(any(QuizDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(put("/api/quizzes/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_deleteQuiz_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(quizService).deleteQuiz(anyInt());

        // Act
        mockMvc.perform(delete("/api/quizzes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(quizService).deleteQuiz(1);
    }
}