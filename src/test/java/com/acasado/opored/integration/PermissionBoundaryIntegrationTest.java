package com.acasado.opored.integration;

import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.MessageEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import com.acasado.opored.repository.ForumRepository;
import com.acasado.opored.repository.MessageRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionBoundaryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void When_DeleteMessage_AsNonOwner_Expect_Exception_ThenRootCanDelete() {
        // Arrange
        StudentEntity owner = createStudent("owner@example.com");
        StudentEntity other = createStudent("other@example.com");

        ForumEntity forum = forumRepository.save(new ForumEntity("Forum", "Desc"));
        TopicEntity topic = new TopicEntity("Topic", com.acasado.opored.enumeration.StatusEnum.VISIBLE, forum, owner);
        topic = topicRepository.save(topic);

        MessageEntity message = new MessageEntity("Content", com.acasado.opored.enumeration.StatusEnum.VISIBLE, null, topic, owner);
        message = messageRepository.save(message);

        authenticateAs(other.getId());

        // Act & Assert
        Integer messageId = message.getId();

        assertThrows(UserWithoutPermissionException.class, () -> messageService.deleteMessage(messageId));

        // Act
        authenticateAs(other.getId(), "ROOT");

        messageService.deleteMessage(message.getId());

        // Assert
        assertTrue(messageRepository.getReferenceById(message.getId()).getIsDeleted());
    }
}