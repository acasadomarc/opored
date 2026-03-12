package com.acasado.opored.util;

import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ModerationTopicFactory {

    public static ModerationTopicDTO createValidModerationTopicDTO() {
        return new ModerationTopicDTO(
                200, // Topic ID
                5,   // Moderator ID
                LocalDate.now(),
                "Inappropriate topic title"
        );
    }

    public static ModerationTopicEntity createValidModerationTopicEntity() {
        ModerationTopicId id = new ModerationTopicId(200, 5);

        TopicEntity topic = new TopicEntity();
        topic.setId(200);
        topic.setStatus(StatusEnum.VISIBLE);
        topic.setStudentsFollowing(new HashSet<>());

        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setId(5);

        ModerationTopicEntity entity = new ModerationTopicEntity();
        entity.setId(id);
        entity.setTopic(topic);
        entity.setModerator(moderator);
        entity.setModerationDate(LocalDate.now());
        entity.setReason("Inappropriate topic title");
        entity.setIsDeleted(false);

        return entity;
    }

    public static TopicEntity createTopicWithFollowers() {
        TopicEntity topic = new TopicEntity();
        topic.setId(200);
        topic.setStatus(StatusEnum.VISIBLE);

        StudentEntity student = new StudentEntity();
        student.setId(50);

        topic.setStudentsFollowing(new HashSet<>());
        topic.getStudentsFollowing().add(student);

        return topic;
    }
}