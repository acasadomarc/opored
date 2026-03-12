package com.acasado.opored.controller;

import com.acasado.opored.controller.base.BaseControllerTest;
import com.acasado.opored.dto.MessageDTO;
import com.acasado.opored.service.MessageService;
import com.acasado.opored.util.MessageFactory;
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

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest extends BaseControllerTest {

    @MockitoBean
    private MessageService messageService;

    @Test
    void When_GetAllMessages_Expect_OkAndList() throws Exception {
        // Arrange
        List<MessageDTO> dtoList = List.of(MessageFactory.createValidMessageDTO());
        when(messageService.getAllMessages()).thenReturn(dtoList);

        // Act
        mockMvc.perform(get("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].content").value("This is a message content"));
    }

    @Test
    void When_GetMessageById_Expect_OkAndDTO() throws Exception {
        // Arrange
        MessageDTO dto = MessageFactory.createValidMessageDTO();
        when(messageService.getMessageById(anyInt())).thenReturn(dto);

        // Act
        mockMvc.perform(get("/api/messages/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void Expect_NotFound_When_GetMessageById_DoesNotExist() throws Exception {
        // Arrange
        when(messageService.getMessageById(anyInt()))
                .thenThrow(new EntityNotFoundException("Message not found"));

        // Act
        mockMvc.perform(get("/api/messages/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());
    }

    @Test
    void When_CreateMessage_Expect_CreatedAndDTO() throws Exception {
        // Arrange
        MessageDTO inputDto = MessageFactory.createValidMessageDTO();
        when(messageService.createMessage(any(MessageDTO.class))).thenReturn(inputDto);

        // Act
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topicId").value(inputDto.getTopicId()));
    }

    @Test
    void Expect_BadRequest_When_CreateMessage_InvalidData() throws Exception {
        // Arrange
        MessageDTO invalidDto = MessageFactory.createInvalidMessageDTO();

        // Act
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                // Assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void When_UpdateMyMessage_Expect_OkAndDTO() throws Exception {
        // Arrange
        MessageDTO updatedDto = MessageFactory.createValidMessageDTO();
        updatedDto.setContent("Updated Content");

        when(messageService.updateMyMessage(anyInt(), anyString(), anyString()))
                .thenReturn(updatedDto);

        // Act
        mockMvc.perform(put("/api/messages/me/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    void When_DeleteMessage_Expect_NoContent() throws Exception {
        // Arrange
        doNothing().when(messageService).deleteMessage(anyInt());

        // Act
        mockMvc.perform(delete("/api/messages/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNoContent());

        verify(messageService).deleteMessage(1);
    }
}