package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.QuestionDTO;
import com.acasado.opored.service.QuestionService;
import com.acasado.opored.util.QuestionFactory;
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

@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuestionControllerTest extends BaseControllerTest {

    @MockitoBean
    private QuestionService questionService;

    @Test
    void When_GetAllQuestions_Expect_OkAndList() throws Exception {
        // Arrange
        List<QuestionDTO> dtoList = List.of(QuestionFactory.createValidQuestionDTO());
        when(questionService.getAllQuestions()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].statement").value("What is the capital of Spain?"));
    }

    @Test
    void When_CreateQuestion_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        QuestionDTO inputDto = QuestionFactory.createValidQuestionDTO();
        when(questionService.createQuestion(any(QuestionDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.testId").value(inputDto.getTestId()));
    }

    @Test
    void Expect_BadRequest_When_CreateQuestion_InvalidData() throws Exception {
        // Arrange
        QuestionDTO invalidDto = QuestionFactory.createInvalidQuestionDTO();

        // Act
        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(questionService, never()).createQuestion(any());
    }

    @Test
    void When_UpdateMyQuestion_Expect_OkAndDTO() throws Exception {
        // Arrange
        QuestionDTO inputDto = QuestionFactory.createValidQuestionDTO();
        inputDto.setStatement("Updated Statement");
        when(questionService.updateQuestion(any(QuestionDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(put("/api/questions/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statement").value("Updated Statement"));
    }

    @Test
    void When_DeleteQuestion_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(questionService).deleteQuestion(anyInt());

        // Act
        mockMvc.perform(delete("/api/questions/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(questionService).deleteQuestion(1);
    }
}