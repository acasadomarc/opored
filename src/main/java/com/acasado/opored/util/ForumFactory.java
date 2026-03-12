package com.acasado.opored.util;

import com.acasado.opored.dto.ForumDTO;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.TopicEntity;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ForumFactory {

    public static ForumDTO createValidForumDTO() {
        return new ForumDTO(
                1,
                "Public Exams Forum",
                "Discussion about exams",
                new HashSet<>()
        );
    }

    public static ForumEntity createValidForumEntity() {
        ForumEntity entity = new ForumEntity();
        entity.setId(1);
        entity.setName("Public Exams Forum");
        entity.setDescription("Discussion about exams");
        entity.setIsDeleted(false);
        entity.setTopics(new HashSet<>());
        return entity;
    }

    public static ForumEntity createForumEntityWithTopics() {
        ForumEntity entity = createValidForumEntity();

        TopicEntity topic = new TopicEntity();
        topic.setId(100);
        topic.setTitle("Math Doubts");

        Set<TopicEntity> topics = new HashSet<>();
        topics.add(topic);

        entity.setTopics(topics);
        return entity;
    }
}