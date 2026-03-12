package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.ModerationMessageDTO;
import com.acasado.opored.service.ModerationMessageService;
import com.acasado.opored.util.ModerationMessageFactory;
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

@WebMvcTest(ModerationMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModerationMessageControllerTest extends BaseControllerTest {

    @MockitoBean
    private ModerationMessageService moderationMessageService;

    @Test
    void When_GetAllModeratedMessages_Expect_OkAndList() throws Exception {
        // Arrange
        List<ModerationMessageDTO> dtoList = List.of(ModerationMessageFactory.createValidModerationMessageDTO());
        when(moderationMessageService.getAllModeratedMessages()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/moderationMessages")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetMyModeratedMessages_Expect_OkAndList() throws Exception {
        // Arrange
        List<ModerationMessageDTO> dtoList = List.of(ModerationMessageFactory.createValidModerationMessageDTO());
        when(moderationMessageService.getMyModeratedMessages()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/moderationMessages/me")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void When_GetModeratedMessageById_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModerationMessageDTO dto = ModerationMessageFactory.createValidModerationMessageDTO();
        when(moderationMessageService.getModerationMessageById(anyInt(), anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/moderationMessages/message/{messageId}/moderator/{moderatorId}", 100, 5)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(100));
    }

    @Test
    void Expect_NotFound_When_GetModeratedMessageById_DoesNotExist() throws Exception {
        // Arrange
        when(moderationMessageService.getModerationMessageById(anyInt(), anyInt()))
                .thenThrow(new EntityNotFoundException("Moderation not found"));

        // Act
        mockMvc.perform(get("/api/moderationMessages/message/{messageId}/moderator/{moderatorId}", 999, 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_ModerateMessage_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        ModerationMessageDTO inputDto = ModerationMessageFactory.createValidModerationMessageDTO();
        when(moderationMessageService.moderateMessage(any(ModerationMessageDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/moderationMessages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value(inputDto.getReason()));
    }

    @Test
    void When_UpdateModeratedMessageByMe_Expect_OkAndDTO() throws Exception {
        // Arrange
        ModerationMessageDTO updatedDto = ModerationMessageFactory.createValidModerationMessageDTO();
        updatedDto.setReason("Updated Reason");

        when(moderationMessageService.updateModeratedMessageByMe(anyInt(), anyString())).thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/moderationMessages/me/message/{messageId}/", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reason").value("Updated Reason"));
    }

    @Test
    void When_DeleteModerationMessage_Expect_Ok() throws Exception {
        // Arrange
        doNothing().when(moderationMessageService).deleteModerationMessage(anyInt(), anyInt());

        // Act
        mockMvc.perform(delete("/api/moderationMessages/message/{messageId}/moderator/{moderatorId}", 100, 5)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().string("Moderación de mensaje eliminada correctamente"));
    }
}