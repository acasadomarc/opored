package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.service.ModerationTopicService;
import com.acasado.opored.util.ModerationTopicFactory;
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

@WebMvcTest(ModerationTopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModerationTopicControllerTest extends BaseControllerTest {

    @MockitoBean
    private ModerationTopicService moderationTopicService;

    @Test
    void When_GetAllModeratedTopics_Expect_OkAndList() throws Exception {
        // Arrange
        List<ModerationTopicDTO> dtoList = List.of(ModerationTopicFactory.createValidModerationTopicDTO());
        when(moderationTopicService.getAllModeratedTopics()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/moderationTopics")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetMyModeratedTopics_Expect_OkAndList() throws Exception {
        // Arrange
        List<ModerationTopicDTO> dtoList = List.of(ModerationTopicFactory.createValidModerationTopicDTO());
        when(moderationTopicService.getMyModeratedTopics()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/moderationTopics/me")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetModeratedTopicById_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModerationTopicDTO dto = ModerationTopicFactory.createValidModerationTopicDTO();
        when(moderationTopicService.getModerationTopicById(anyInt(), anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/moderationTopics/topic/{topicId}/moderator/{moderatorId}", 200, 5)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(200));
    }

    @Test
    void Expect_NotFound_When_GetModeratedTopicById_DoesNotExist() throws Exception {
        // Arrange
        when(moderationTopicService.getModerationTopicById(anyInt(), anyInt()))
                .thenThrow(new EntityNotFoundException("Moderation not found"));

        // Act
        mockMvc.perform(get("/api/moderationTopics/topic/{topicId}/moderator/{moderatorId}", 999, 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_ModerateTopic_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        ModerationTopicDTO inputDto = ModerationTopicFactory.createValidModerationTopicDTO();
        when(moderationTopicService.moderateTopic(any(ModerationTopicDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/moderationTopics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value(inputDto.getReason()));
    }

    @Test
    void When_UpdateModeratedTopicByMe_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModerationTopicDTO updatedDto = ModerationTopicFactory.createValidModerationTopicDTO();
        updatedDto.setReason("Updated Reason");

        when(moderationTopicService.updateModeratedTopicByMe(anyInt(), anyString())).thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/moderationTopics/me/topic/{topicId}", 200)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Updated Reason"));
    }

    @Test
    void When_DeleteModerationTopic_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(moderationTopicService).deleteModerationTopic(anyInt(), anyInt());

        // Act
        mockMvc.perform(delete("/api/moderationTopics/topic/{topicId}/moderator/{moderatorId}", 200, 5)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Moderación de tema eliminada correctamente"));
    }
}