package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.dto.TopicDTO;
import com.acasado.opored.service.TopicService;
import com.acasado.opored.util.TopicFactory;
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

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicControllerTest extends BaseControllerTest {

    @MockitoBean
    private TopicService topicService;

    @Test
    void When_GetAllTopics_Expect_OkAndList() throws Exception {
        // Arrange
        List<TopicDTO> dtoList = List.of(TopicFactory.createValidTopicDTO());
        when(topicService.getAllTopics()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Doubts about Math"));
    }

    @Test
    void When_GetTopicById_Expect_OkAndDTO() throws Exception {
        // Arrange
        TopicDTO dto = TopicFactory.createValidTopicDTO();
        when(topicService.getTopicById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/topics/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetTopicById_DoesNotExist() throws Exception {
        // Arrange
        when(topicService.getTopicById(anyInt()))
                .thenThrow(new EntityNotFoundException("Topic not found"));

        // Act
        mockMvc.perform(get("/api/topics/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateTopic_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        TopicDTO inputDto = TopicFactory.createValidTopicDTO();
        when(topicService.createTopic(any(TopicDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.forumId").value(inputDto.getForumId()));
    }

    @Test
    void Expect_BadRequest_When_CreateTopic_InvalidData() throws Exception {
        // Arrange
        TopicDTO invalidDto = TopicFactory.createInvalidTopicDTO();

        // Act
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void When_UpdateMyTopic_Expect_OkAndDTO() throws Exception {
        // Arrange
        TopicDTO updatedDto = TopicFactory.createValidTopicDTO();
        updatedDto.setTitle("Updated Title");

        when(topicService.updateMyTopic(anyInt(), anyString(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/topics/me/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void When_DeleteTopic_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(topicService).deleteTopic(anyInt());

        // Act
        mockMvc.perform(delete("/api/topics/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(topicService).deleteTopic(1);
    }

    @Test
    void When_GetFollowingStudents_Expect_OkAndList() throws Exception {
        // Arrange
        Set<StudentSummaryDTO> students = Set.of(new StudentSummaryDTO());
        when(topicService.getStudentsFollowing(anyInt())).thenReturn(students);

        // Act
        mockMvc.perform(get("/api/topics/followingTopic/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}