package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.AnswerDTO;
import com.acasado.opored.service.AnswerService;
import com.acasado.opored.util.AnswerFactory;
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

@WebMvcTest(AnswerController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnswerControllerTest extends BaseControllerTest {

    @MockitoBean
    private AnswerService answerService;

    @Test
    void When_GetAllAnswers_Expect_OkAndList() throws Exception {
        // Arrange
        List<AnswerDTO> dtoList = List.of(AnswerFactory.createValidAnswerDTO());
        when(answerService.getAllAnswers()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].reply").value("The answer is 42"));
    }

    @Test
    void When_CreateAnswer_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        AnswerDTO inputDto = AnswerFactory.createValidAnswerDTO();
        when(answerService.createAnswer(any(AnswerDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(inputDto.getQuestionId()));
    }

    @Test
    void Expect_BadRequest_When_CreateAnswer_InvalidData() throws Exception {
        // Arrange
        AnswerDTO invalidDto = AnswerFactory.createInvalidAnswerDTO();

        // Act
        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());

        verify(answerService, never()).createAnswer(any());
    }

    @Test
    void When_UpdateMyAnswer_Expect_OkAndDTO() throws Exception {
        // Arrange
        AnswerDTO inputDto = AnswerFactory.createValidAnswerDTO();
        inputDto.setReply("Updated Reply");
        when(answerService.updateAnswer(any(AnswerDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(put("/api/answers/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Updated Reply"));
    }

    @Test
    void When_DeleteAnswer_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(answerService).deleteAnswer(anyInt());

        // Act
        mockMvc.perform(delete("/api/answers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(answerService).deleteAnswer(1);
    }
}