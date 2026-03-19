package com.acasado.opored.integration;

import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.FollowTopicRepository;
import com.acasado.opored.repository.ForumRepository;
import com.acasado.opored.repository.ModerationTopicRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.service.ModerationTopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ModerationTopicIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ModerationTopicService moderationTopicService;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ModerationTopicRepository moderationTopicRepository;

    @Autowired
    private FollowTopicRepository followTopicRepository;

    @Test
    void When_ModerateTopic_Expect_HiddenAndUnfollow() {
        // Arrange
        StudentEntity author = createStudent("author@example.com");
        StudentEntity follower = createStudent("follower@example.com");
        ModeratorEntity moderator = createModerator("moderator@example.com");

        ForumEntity forum = forumRepository.save(new ForumEntity( "Forum", "Desc"));
        TopicEntity topic = new TopicEntity("Topic", StatusEnum.VISIBLE, forum, author);
        topic = topicRepository.save(topic);

        FollowTopic followTopic = new FollowTopic(new FollowTopicId(topic.getId(), follower.getId()), topic, follower);
        topic.getStudentsFollowing().add(follower);
        followTopicRepository.save(followTopic);

        ModerationTopicDTO request = new ModerationTopicDTO(topic.getId(), moderator.getId(), null, "Off-topic");

        // Act
        ModerationTopicDTO result = moderationTopicService.moderateTopic(request);

        TopicEntity updatedTopic = topicRepository.findById(topic.getId()).orElseThrow();

        // Assert
        assertEquals(StatusEnum.HIDDEN, updatedTopic.getStatus());
        assertTrue(moderationTopicRepository.findById(new ModerationTopicId(result.getTopicId(), result.getModeratorId())).isPresent());
        assertFalse(followTopicRepository.existsById(new FollowTopicId(topic.getId(), follower.getId())));
    }

    @Test
    void When_UpdateAndDeleteModeration_Expect_StatusRestored() {
        // Arrange
        StudentEntity author = createStudent("author2@example.com");
        ModeratorEntity moderator = createModerator("moderator2@example.com");

        ForumEntity forum = forumRepository.save(new ForumEntity("Forum", "Desc"));
        TopicEntity topic = new TopicEntity("Topic", StatusEnum.VISIBLE, forum, author);
        topic = topicRepository.save(topic);

        ModerationTopicDTO request = new ModerationTopicDTO(topic.getId(), moderator.getId(), null, "Reason");
        moderationTopicService.moderateTopic(request);

        authenticateAs(moderator.getId());

        // Act
        ModerationTopicDTO updated = moderationTopicService.updateModeratedTopicByMe(topic.getId(), "Updated");

        // Assert
        assertEquals("Updated", updated.getReason());

        // Act
        moderationTopicService.deleteModerationTopic(topic.getId(), moderator.getId());

        // Assert
        TopicEntity restoredTopic = topicRepository.findById(topic.getId()).orElseThrow();
        assertEquals(StatusEnum.VISIBLE, restoredTopic.getStatus());
    }
}