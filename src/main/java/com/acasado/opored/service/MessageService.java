package com.acasado.opored.service;

import com.acasado.opored.dto.MessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.model.MessageEntity;
import com.acasado.opored.model.UserEntity;
import com.acasado.opored.repository.MessageRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.repository.UserRepository;
import com.acasado.opored.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll().stream().map(this::convertToMessageDTO).toList();
    }

    public MessageDTO getMessageById(Integer id) {
        MessageEntity message = messageRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToMessageDTO(message);
    }

    public MessageDTO createMessage(MessageDTO messageDTO) {
        if (!topicRepository.existsById(messageDTO.getTopicId())) {
            throw new EntityNotFoundException("Topic with id " + messageDTO.getTopicId() + " not found");
        }
        if (!userRepository.existsById(messageDTO.getUserId())) {
            throw new EntityNotFoundException("User with id " + messageDTO.getUserId() + " not found");
        }
        MessageEntity message = convertToMessage(messageDTO);
        MessageEntity savedMessage = messageRepository.save(message);
        return convertToMessageDTO(savedMessage);
    }

    public MessageDTO updateMyMessage(Integer id, String content, String status) {
        MessageEntity toUpdateMessage = messageRepository.findById(id).orElseThrow(() -> notFoundById(id));

        if (!toUpdateMessage.getUser().getId().equals(getCurrentUserId())) {
            throw new UserWithoutPermissionException("You do not have permissions to update this message");
        }

        StatusEnum statusEnum = StatusEnum.valueOf(status);
        toUpdateMessage.setContent(content);
        toUpdateMessage.setStatus(statusEnum);

        MessageEntity updatedMessage = messageRepository.save(toUpdateMessage);
        return convertToMessageDTO(updatedMessage);
    }

    public void deleteMessage(Integer id) {
        MessageEntity toDeleteMessage = messageRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        if (!isAuthorized(toDeleteMessage.getUser().getId())) {
            throw new UserWithoutPermissionException("You do not have permissions to delete this message");
        }
        // Messages are not deleted, only the status changes
        toDeleteMessage.setStatus(StatusEnum.DELETED);
        messageRepository.save(toDeleteMessage);
    }

    public void hideMessageInModeratedTopic(Integer id) {
        MessageEntity message = messageRepository.findById(id).orElseThrow(() -> notFoundById(id));
        message.setStatus(StatusEnum.HIDDEN);
        messageRepository.save(message);
    }

    public void changeMessagesOwner(Set<MessageEntity> messages, UserEntity user) {
        Set<MessageEntity> changedOwnershipMessages = new HashSet<>();
        for (MessageEntity messageEntity : messages) {
            messageEntity.setUser(user);
            changedOwnershipMessages.add(messageEntity);
        }
        messageRepository.saveAll(changedOwnershipMessages);
    }

    private MessageDTO convertToMessageDTO(MessageEntity message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getStatus().toString(),
                message.getPublicationDate(),
                message.getParentMessage() != null ? message.getParentMessage().getId() : null,
                message.getTopic().getId(),
                message.getUser().getId()
        );
    }

    private MessageEntity convertToMessage(MessageDTO messageDTO) {
        return new MessageEntity(
                messageDTO.getContent(),
                StatusEnum.valueOf(messageDTO.getStatus()),
                messageDTO.getParentMessageId() != null
                        ? messageRepository.findById(messageDTO.getParentMessageId())
                        .orElseThrow(() -> notFoundById(messageDTO.getParentMessageId()))
                        : null,
                topicRepository.getReferenceById(messageDTO.getTopicId()),
                userRepository.getReferenceById(messageDTO.getUserId())
        );
    }

    private Integer getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private boolean isAuthorized(Integer userId) {
        return SecurityUtils.isUserAdmin() || SecurityUtils.isProvidedUser(userId);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Message with id %d not found", id));
    }
}
