package com.acasado.opored.service;

import com.acasado.opored.dto.ModerationMessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.repository.MessageRepository;
import com.acasado.opored.repository.ModerationMessageRepository;
import com.acasado.opored.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ModerationMessageService {

    private final ModeratorRepository moderatorRepository;
    private final MessageRepository messageRepository;
    private final ModerationMessageRepository moderationMessageRepository;

    public List<ModerationMessageDTO> getAllModeratedMessages() {
        return moderationMessageRepository.findAll().stream().map(this::convertToModerationMessageDTO).toList();
    }

    public List<ModerationMessageDTO> getMyModeratedMessages() {
        return moderationMessageRepository.findAll().stream().filter(moderationMessageEntity -> Objects.equals(moderationMessageEntity.getModerator().getId(), getCurrentModeratorUserId())).map(this::convertToModerationMessageDTO).toList();
    }

    public ModerationMessageDTO getModerationMessageById(Integer messageId, Integer moderatorId) {
        ModerationMessageId moderationMessageId = new ModerationMessageId(messageId, moderatorId);
        ModerationMessageEntity moderatedMessage = moderationMessageRepository.findById(moderationMessageId).orElseThrow(() -> notFoundById(messageId));

        return convertToModerationMessageDTO(moderatedMessage);
    }

    public ModerationMessageDTO moderateMessage(ModerationMessageDTO moderationMessageDTO) {
        MessageEntity message = messageRepository.findById(moderationMessageDTO.getMessageId()).orElseThrow(() -> new EntityNotFoundException("Message with id " + moderationMessageDTO.getMessageId() + " not found"));
        moderatorRepository.findById(moderationMessageDTO.getModeratorId()).orElseThrow(() -> new EntityNotFoundException("Moderator with id " + moderationMessageDTO.getModeratorId() + " not found"));

        ModerationMessageEntity moderatedMessage = convertToModerationMessage(moderationMessageDTO);
        moderationMessageRepository.save(moderatedMessage);

        message.setStatus(StatusEnum.HIDDEN);
        messageRepository.save(message);

        return convertToModerationMessageDTO(moderatedMessage);
    }

    public ModerationMessageDTO updateModeratedMessageByMe(Integer messageId, String reason) {
        Integer currentId = getCurrentModeratorUserId();
        ModerationMessageId moderationMessageId = new ModerationMessageId(messageId, currentId);
        ModerationMessageEntity toUpdateModerationMessage = moderationMessageRepository.findById(moderationMessageId)
                .orElseThrow(() -> notFoundById(messageId));

        toUpdateModerationMessage.setReason(reason);

        ModerationMessageEntity updatedModerationMessage = moderationMessageRepository.save(toUpdateModerationMessage);
        return convertToModerationMessageDTO(updatedModerationMessage);
    }

    public void deleteModerationMessage(Integer messageId, Integer moderatorId) {
        ModerationMessageId moderationMessageId = new ModerationMessageId(messageId, moderatorId);
        ModerationMessageEntity toDeleteModerationMessage = moderationMessageRepository.findById(moderationMessageId)
                .orElseThrow(() -> notFoundById(messageId));

        // Logical delete
        toDeleteModerationMessage.setIsDeleted(true);
        moderationMessageRepository.save(toDeleteModerationMessage);

        MessageEntity message = messageRepository.getReferenceById(messageId);
        message.setStatus(StatusEnum.VISIBLE);
        messageRepository.save(message);
    }

    private ModerationMessageDTO convertToModerationMessageDTO(ModerationMessageEntity moderationMessageEntity) {
        return new ModerationMessageDTO(
                moderationMessageEntity.getId().getMessageId(),
                moderationMessageEntity.getId().getModeratorId(),
                moderationMessageEntity.getModerationDate(),
                moderationMessageEntity.getReason());
    }

    private ModerationMessageEntity convertToModerationMessage(ModerationMessageDTO moderationMessageDTO) {
        return new ModerationMessageEntity(
                new ModerationMessageId(moderationMessageDTO.getMessageId(), moderationMessageDTO.getModeratorId()),
                messageRepository.getReferenceById(moderationMessageDTO.getMessageId()),
                moderatorRepository.getReferenceById(moderationMessageDTO.getModeratorId()),
                moderationMessageDTO.getReason());
    }

    private Integer getCurrentModeratorUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Moderated message with id %d not found", id));
    }
}
