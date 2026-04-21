package com.acasado.opored.util;

import com.acasado.opored.dto.MessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.MessageEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import com.acasado.opored.model.UserEntity;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MessageFactory {

    public static MessageDTO createValidMessageDTO() {
        return new MessageDTO(
                1,
                "This is a message content",
                StatusEnum.VISIBLE.toString(),
                Timestamp.from(Instant.now()),
                null, // Parent Message ID
                10,   // Topic ID
                5     // User ID
        );
    }

    public static MessageDTO createInvalidMessageDTO() {
        return new MessageDTO(
                null,
                null,
                StatusEnum.VISIBLE.toString(),
                null,
                null,
                null,
                null
        );
    }

    public static MessageEntity createValidMessageEntity() {
        TopicEntity topic = new TopicEntity();
        topic.setId(10);

        UserEntity user = new StudentEntity();
        user.setId(5);

        MessageEntity entity = new MessageEntity();
        entity.setId(1);
        entity.setContent("This is a message content");
        entity.setStatus(StatusEnum.VISIBLE);
        entity.setPublicationDate(Timestamp.from(Instant.now()));
        entity.setTopic(topic);
        entity.setUser(user);
        entity.setIsDeleted(false);

        return entity;
    }

    // Helper for Security tests
    public static MessageEntity createMessageWithUser(Integer userId) {
        MessageEntity entity = createValidMessageEntity();
        UserEntity user = new StudentEntity();
        user.setId(userId);
        entity.setUser(user);
        return entity;
    }
}