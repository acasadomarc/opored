package com.acasado.opored.service;

import com.acasado.opored.dto.ModerationTopicDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.ModerationTopicRepository;
import com.acasado.opored.repository.ModeratorRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.util.ModerationTopicFactory;
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
class ModerationTopicServiceTest {

    @Mock private ModeratorRepository moderatorRepository;
    @Mock private TopicRepository topicRepository;
    @Mock private ModerationTopicRepository moderationTopicRepository;
    @Mock private StudentService studentService;

    @InjectMocks
    private ModerationTopicService moderationTopicService;

    // --- GetAll ---
    @Test
    void When_GetAllModeratedTopics_Expect_List() {
        when(moderationTopicRepository.findAll()).thenReturn(List.of(ModerationTopicFactory.createValidModerationTopicEntity()));
        List<ModerationTopicDTO> result = moderationTopicService.getAllModeratedTopics();
        assertFalse(result.isEmpty());
    }

    // --- GetMyTopics (Filtering Logic) ---
    @Test
    void When_GetMyModeratedTopics_Expect_FilteredList() {
        int myModeratorId = 5;
        ModerationTopicEntity entity = ModerationTopicFactory.createValidModerationTopicEntity();
        // ID in Factory is (200, 5), so it matches

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(myModeratorId);
            when(moderationTopicRepository.findAll()).thenReturn(List.of(entity));

            List<ModerationTopicDTO> result = moderationTopicService.getMyModeratedTopics();

            assertFalse(result.isEmpty());
            assertEquals(myModeratorId, result.getFirst().getModeratorId());
        }
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        ModerationTopicEntity entity = ModerationTopicFactory.createValidModerationTopicEntity();
        ModerationTopicId id = new ModerationTopicId(200, 5);

        when(moderationTopicRepository.findById(id)).thenReturn(Optional.of(entity));

        ModerationTopicDTO result = moderationTopicService.getModerationTopicById(200, 5);
        assertNotNull(result);
    }

    // --- Moderate (Create & Side Effects) ---
    @Test
    void When_ModerateTopic_Expect_HiddenStatusAndUnfollow() {
        // Arrange
        ModerationTopicDTO inputDto = ModerationTopicFactory.createValidModerationTopicDTO();

        // Topic has followers to test the loop
        TopicEntity topic = ModerationTopicFactory.createTopicWithFollowers();
        int studentId = topic.getStudentsFollowing().iterator().next().getId();

        when(topicRepository.findById(inputDto.getTopicId())).thenReturn(Optional.of(topic));
        when(moderatorRepository.findById(inputDto.getModeratorId())).thenReturn(Optional.of(new ModeratorEntity()));

        // Mock references
        when(topicRepository.getReferenceById(anyInt())).thenReturn(topic);
        when(moderatorRepository.getReferenceById(anyInt())).thenReturn(new ModeratorEntity());

        when(moderationTopicRepository.save(any(ModerationTopicEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ModerationTopicDTO result = moderationTopicService.moderateTopic(inputDto);

        // Assert
        assertNotNull(result);

        assertEquals(StatusEnum.HIDDEN, topic.getStatus());
        verify(topicRepository).save(topic);

        // Verify Cascade unfollow call
        verify(studentService).unfollowDeletedTopic(studentId, topic.getId());

        verify(moderationTopicRepository).save(any(ModerationTopicEntity.class));
    }

    @Test
    void Expect_Exception_When_Moderate_TopicNotFound() {
        ModerationTopicDTO inputDto = ModerationTopicFactory.createValidModerationTopicDTO();
        when(topicRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moderationTopicService.moderateTopic(inputDto));
        verify(moderationTopicRepository, never()).save(any());
    }

    // --- Update By Me ---
    @Test
    void When_UpdateByMe_Expect_Success() {
        int myId = 5;
        int topicId = 200;
        ModerationTopicEntity entity = ModerationTopicFactory.createValidModerationTopicEntity();
        ModerationTopicId id = new ModerationTopicId(topicId, myId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(myId);

            when(moderationTopicRepository.findById(id)).thenReturn(Optional.of(entity));
            when(moderationTopicRepository.save(any(ModerationTopicEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            ModerationTopicDTO result = moderationTopicService.updateModeratedTopicByMe(topicId, "New Reason");

            // Assert
            assertEquals("New Reason", result.getReason());
            verify(moderationTopicRepository).save(entity);
        }
    }

    @Test
    void Expect_Exception_When_GetById_NotFound() {
        when(moderationTopicRepository.findById(any(ModerationTopicId.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> moderationTopicService.getModerationTopicById(999, 999));
    }

    @Test
    void Expect_Exception_When_Moderate_ModeratorNotFound() {
        ModerationTopicDTO inputDto = ModerationTopicFactory.createValidModerationTopicDTO();
        when(topicRepository.findById(anyInt())).thenReturn(Optional.of(new TopicEntity()));
        when(moderatorRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moderationTopicService.moderateTopic(inputDto));
    }

    @Test
    void Expect_Exception_When_UpdateByMe_NotFound() {
        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(5);
            when(moderationTopicRepository.findById(any(ModerationTopicId.class))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> moderationTopicService.updateModeratedTopicByMe(200, "Reason"));
        }
    }

    @Test
    void Expect_Exception_When_DeleteModeration_NotFound() {
        when(moderationTopicRepository.findById(any(ModerationTopicId.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> moderationTopicService.deleteModerationTopic(200, 5));
    }

    @Test
    void When_DeleteMyModerationTopic_Expect_Success() {
        int modId = 5;
        int topicId = 200;
        ModerationTopicEntity entity = ModerationTopicFactory.createValidModerationTopicEntity();
        
        // Match expectation of deleteMyModerationTopic
        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setId(modId);
        entity.setModerator(moderator);
        
        TopicEntity topic = new TopicEntity();
        topic.setId(topicId);
        
        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(modId);
            when(moderationTopicRepository.findById(any(ModerationTopicId.class))).thenReturn(Optional.of(entity));
            when(topicRepository.findById(anyInt())).thenReturn(Optional.of(topic));

            moderationTopicService.deleteMyModerationTopic(topicId);

            assertEquals(StatusEnum.VISIBLE, topic.getStatus());
            verify(moderationTopicRepository).delete(entity);
        }
    }

    @Test
    void When_ChangeModerationTopicsOwner_Expect_UpdatedOwner() {
        ModeratorEntity newModerator = new ModeratorEntity();
        ModerationTopicEntity entity = ModerationTopicFactory.createValidModerationTopicEntity();
        java.util.Set<ModerationTopicEntity> entities = new java.util.HashSet<>(List.of(entity));

        moderationTopicService.changeModerationTopicsOwner(entities, newModerator);

        assertEquals(newModerator, entity.getModerator());
        verify(moderationTopicRepository).saveAll(any());
    }
}