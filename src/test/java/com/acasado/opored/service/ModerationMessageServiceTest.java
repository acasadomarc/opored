package com.acasado.opored.service;

import com.acasado.opored.dto.ModerationMessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.MessageRepository;
import com.acasado.opored.repository.ModerationMessageRepository;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.util.ModerationMessageFactory;
import com.acasado.opored.util.SecurityUtils;
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
class ModerationMessageServiceTest {

    @Mock private ModeratorRepository moderatorRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ModerationMessageRepository moderationMessageRepository;

    @InjectMocks
    private ModerationMessageService moderationMessageService;

    // --- GetAll ---
    @Test
    void When_GetAllModeratedMessages_Expect_List() {
        when(moderationMessageRepository.findAll()).thenReturn(List.of(ModerationMessageFactory.createValidModerationMessageEntity()));
        List<ModerationMessageDTO> result = moderationMessageService.getAllModeratedMessages();
        assertFalse(result.isEmpty());
    }

    // --- GetMyMessages ---
    @Test
    void When_GetMyModeratedMessages_Expect_FilteredList() {
        // Arrange
        int myModeratorId = 5;
        ModerationMessageEntity entity = ModerationMessageFactory.createValidModerationMessageEntity();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(myModeratorId);
            when(moderationMessageRepository.findAll()).thenReturn(List.of(entity));

            // Act
            List<ModerationMessageDTO> result = moderationMessageService.getMyModeratedMessages();

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(myModeratorId, result.getFirst().getModeratorId());
        }
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        ModerationMessageEntity entity = ModerationMessageFactory.createValidModerationMessageEntity();
        ModerationMessageId id = new ModerationMessageId(100, 5);

        when(moderationMessageRepository.findById(id)).thenReturn(Optional.of(entity));

        ModerationMessageDTO result = moderationMessageService.getModerationMessageById(100, 5);
        assertNotNull(result);
    }

    // --- Moderate (Create) ---
    @Test
    void When_ModerateMessage_Expect_SuccessAndHiddenStatus() {
        // Arrange
        ModerationMessageDTO inputDto = ModerationMessageFactory.createValidModerationMessageDTO();
        MessageEntity message = new MessageEntity();
        message.setId(inputDto.getMessageId());
        message.setStatus(StatusEnum.VISIBLE); // Initially visible

        when(messageRepository.findById(inputDto.getMessageId())).thenReturn(Optional.of(message));
        when(moderatorRepository.findById(inputDto.getModeratorId())).thenReturn(Optional.of(new ModeratorEntity()));

        // Mock references for conversion
        when(messageRepository.getReferenceById(anyInt())).thenReturn(message);
        when(moderatorRepository.getReferenceById(anyInt())).thenReturn(new ModeratorEntity());

        // Mock save
        when(moderationMessageRepository.save(any(ModerationMessageEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ModerationMessageDTO result = moderationMessageService.moderateMessage(inputDto);

        // Assert
        assertNotNull(result);

        // Verify Message status update
        assertEquals(StatusEnum.HIDDEN, message.getStatus());
        verify(messageRepository).save(message);

        // Verify Moderation creation
        verify(moderationMessageRepository).save(any(ModerationMessageEntity.class));
    }

    @Test
    void Expect_Exception_When_Moderate_MessageNotFound() {
        ModerationMessageDTO inputDto = ModerationMessageFactory.createValidModerationMessageDTO();
        when(messageRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moderationMessageService.moderateMessage(inputDto));
        verify(moderationMessageRepository, never()).save(any());
    }

    // --- Update By Me ---
    @Test
    void When_UpdateByMe_Expect_Success() {
        // Arrange
        int myId = 5;
        int msgId = 100;
        ModerationMessageEntity entity = ModerationMessageFactory.createValidModerationMessageEntity();
        ModerationMessageId id = new ModerationMessageId(msgId, myId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(myId);

            when(moderationMessageRepository.findById(id)).thenReturn(Optional.of(entity));
            when(moderationMessageRepository.save(any(ModerationMessageEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            ModerationMessageDTO result = moderationMessageService.updateModeratedMessageByMe(msgId, "New Reason");

            // Assert
            assertEquals("New Reason", result.getReason());
            verify(moderationMessageRepository).save(entity);
        }
    }

    // --- Delete ---
    @Test
    void When_DeleteModeration_Expect_LogicalDeleteAndVisibleStatus() {
        // Arrange
        int msgId = 100;
        int modId = 5;
        ModerationMessageEntity entity = ModerationMessageFactory.createValidModerationMessageEntity();
        ModerationMessageId id = new ModerationMessageId(msgId, modId);

        MessageEntity messageProxy = new MessageEntity();
        messageProxy.setId(msgId);
        messageProxy.setStatus(StatusEnum.HIDDEN);

        when(moderationMessageRepository.findById(id)).thenReturn(Optional.of(entity));
        when(messageRepository.getReferenceById(msgId)).thenReturn(messageProxy);

        // Act
        moderationMessageService.deleteModerationMessage(msgId, modId);

        // Assert
        // 1. Verify Logical Delete of Moderation
        assertTrue(entity.getIsDeleted());
        verify(moderationMessageRepository).save(entity);

        // 2. Verify Message Restoration to Visible
        assertEquals(StatusEnum.VISIBLE, messageProxy.getStatus());
        verify(messageRepository).save(messageProxy);
    }
}