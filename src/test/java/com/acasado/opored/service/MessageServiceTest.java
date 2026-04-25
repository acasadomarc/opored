package com.acasado.opored.service;

import com.acasado.opored.dto.MessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.model.MessageEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.MessageRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.repository.UserRepository;
import com.acasado.opored.util.MessageFactory;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    // --- GetAll ---
    @Test
    void When_GetAllMessages_Expect_List() {
        when(messageRepository.findAll()).thenReturn(List.of(MessageFactory.createValidMessageEntity()));
        List<MessageDTO> result = messageService.getAllMessages();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        MessageEntity entity = MessageFactory.createValidMessageEntity();
        when(messageRepository.findById(1)).thenReturn(Optional.of(entity));

        MessageDTO result = messageService.getMessageById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(messageRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessageById(999));
    }

    // --- Create ---
    @Test
    void When_CreateMessage_Expect_Success() {
        // Arrange
        MessageDTO inputDto = MessageFactory.createValidMessageDTO();
        MessageEntity savedEntity = MessageFactory.createValidMessageEntity();

        // Mock existence checks
        when(topicRepository.existsById(inputDto.getTopicId())).thenReturn(true);
        when(userRepository.existsById(inputDto.getUserId())).thenReturn(true);

        // Mock references (needed for entity conversion inside service)
        when(topicRepository.getReferenceById(anyInt())).thenReturn(new TopicEntity());
        when(userRepository.getReferenceById(anyInt())).thenReturn(new StudentEntity());

        when(messageRepository.save(any(MessageEntity.class))).thenReturn(savedEntity);

        // Act
        MessageDTO result = messageService.createMessage(inputDto);

        // Assert
        assertNotNull(result);
        verify(messageRepository).save(any(MessageEntity.class));
    }

    @Test
    void Expect_Exception_When_Create_TopicNotFound() {
        MessageDTO inputDto = MessageFactory.createValidMessageDTO();
        when(topicRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> messageService.createMessage(inputDto));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void Expect_Exception_When_Create_UserNotFound() {
        MessageDTO inputDto = MessageFactory.createValidMessageDTO();
        when(topicRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> messageService.createMessage(inputDto));
        verify(messageRepository, never()).save(any());
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateMyMessage_Owner_Expect_Success() {
        // Arrange
        int userId = 5;
        MessageEntity entity = MessageFactory.createMessageWithUser(userId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(messageRepository.findById(1)).thenReturn(Optional.of(entity));
            when(messageRepository.save(any(MessageEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            MessageDTO result = messageService.updateMyMessage(1, "New Content", "HIDDEN");

            // Assert
            assertEquals("New Content", result.getContent());
            assertEquals(StatusEnum.HIDDEN, StatusEnum.valueOf(result.getStatus()));
        }
    }

    @Test
    void Expect_Exception_When_UpdateMyMessage_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        MessageEntity entity = MessageFactory.createMessageWithUser(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(messageRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(UserWithoutPermissionException.class, () ->
                    messageService.updateMyMessage(1, "Content", "VISIBLE"));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteMessage_Owner_Expect_LogicalDelete() {
        // Arrange
        int userId = 5;
        MessageEntity entity = MessageFactory.createMessageWithUser(userId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic used inside isAuthorized()
            securityMock.when(() -> SecurityUtils.isProvidedUser(userId)).thenReturn(true);

            when(messageRepository.findById(1)).thenReturn(Optional.of(entity));
            when(messageRepository.getReferenceById(1)).thenReturn(entity);

            // Act
            messageService.deleteMessage(1);

            // Assert
            assertEquals(StatusEnum.DELETED.name(),
                    messageRepository.getReferenceById(entity.getId()).getStatus().name());
            verify(messageRepository).save(entity);
        }
    }

    @Test
    void When_HideMessageInModeratedTopic_Expect_StatusHidden() {
        MessageEntity entity = MessageFactory.createValidMessageEntity();
        when(messageRepository.findById(1)).thenReturn(Optional.of(entity));

        messageService.hideMessageInModeratedTopic(1);

        assertEquals(StatusEnum.HIDDEN, entity.getStatus());
        verify(messageRepository).save(entity);
    }

    @Test
    void When_ChangeMessagesOwner_Expect_UpdatedOwner() {
        UserEntity newUser = new StudentEntity();
        MessageEntity entity = MessageFactory.createValidMessageEntity();
        java.util.Set<MessageEntity> entities = new java.util.HashSet<>(List.of(entity));

        messageService.changeMessagesOwner(entities, newUser);

        assertEquals(newUser, entity.getUser());
        verify(messageRepository).saveAll(any());
    }
}