package com.acasado.opored.util;

import com.acasado.opored.dto.TopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TopicFactory {

    public static TopicDTO createValidTopicDTO() {
        TopicDTO dto = new TopicDTO();
        dto.setId(1);
        dto.setTitle("Doubts about Math");
        dto.setStatus(StatusEnum.VISIBLE.toString());
        dto.setPublicationDate(LocalDate.now());
        dto.setForumId(10);
        dto.setUserId(5);
        dto.setMessages(new HashSet<>());
        return dto;
    }

    public static TopicDTO createInvalidTopicDTO() {
        TopicDTO dto = new TopicDTO();
        dto.setTitle(null); // Invalid
        return dto;
    }

    public static TopicEntity createValidTopicEntity() {
        ForumEntity forum = new ForumEntity();
        forum.setId(10);

        UserEntity user = new StudentEntity();
        user.setId(5);

        TopicEntity topic = new TopicEntity();
        topic.setId(1);
        topic.setTitle("Doubts about Math");
        topic.setStatus(StatusEnum.VISIBLE);
        topic.setPublicationDate(LocalDate.now());
        topic.setForum(forum);
        topic.setUser(user);
        topic.setIsDeleted(false);
        topic.setMessages(new HashSet<>());
        topic.setStudentsFollowing(new HashSet<>());

        return topic;
    }

    public static TopicEntity createTopicWithMessagesAndFollowers() {
        TopicEntity topic = createValidTopicEntity();

        // Add a message
        MessageEntity message = new MessageEntity();
        message.setId(100);
        topic.getMessages().add(message);

        // Add a follower
        StudentEntity student = new StudentEntity();
        student.setId(50);
        student.setEnabled(true);
        topic.getStudentsFollowing().add(student);

        return topic;
    }
}