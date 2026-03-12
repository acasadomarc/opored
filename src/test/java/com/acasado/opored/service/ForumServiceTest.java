package com.acasado.opored.service;

import com.acasado.opored.dto.ForumDTO;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.repository.ForumRepository;
import com.acasado.opored.util.ForumFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private TopicService topicService;

    @InjectMocks
    private ForumService forumService;

    @Test
    void When_GetAllForums_Expect_ListDTO() {
        // Arrange
        List<ForumEntity> entities = List.of(ForumFactory.createValidForumEntity());
        when(forumRepository.findAll()).thenReturn(entities);

        // Act
        List<ForumDTO> result = forumService.getAllForums();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getName(), result.getFirst().getName());
    }

    @Test
    void When_GetForumById_Expect_DTO() {
        // Arrange
        ForumEntity entity = ForumFactory.createValidForumEntity();
        when(forumRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        ForumDTO result = forumService.getForumById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetForumById_NotFound() {
        // Arrange
        when(forumRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> forumService.getForumById(999));
    }

    @Test
    void When_CreateForum_Expect_Entity() {
        // Arrange
        ForumEntity inputEntity = ForumFactory.createValidForumEntity();
        when(forumRepository.save(any(ForumEntity.class))).thenReturn(inputEntity);

        // Act
        ForumEntity result = forumService.createForum(inputEntity);

        // Assert
        assertNotNull(result);
        assertEquals(inputEntity.getName(), result.getName());
    }

    @Test
    void When_UpdateForum_Expect_UpdatedDTO() {
        // Arrange
        ForumEntity entity = ForumFactory.createValidForumEntity();
        String newName = "New Name";

        when(forumRepository.findById(1)).thenReturn(Optional.of(entity));
        when(forumRepository.save(any(ForumEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ForumDTO result = forumService.updateForum(1, newName, "New Desc");

        // Assert
        assertEquals(newName, result.getName());
        verify(forumRepository).save(entity);
    }

    @Test
    void When_DeleteForum_Expect_LogicalDeleteAndCascade() {
        // Arrange
        ForumEntity entity = ForumFactory.createForumEntityWithTopics();
        int topicId = entity.getTopics().iterator().next().getId();

        when(forumRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        forumService.deleteForum(1);

        // Assert
        assertTrue(entity.getIsDeleted());
        verify(forumRepository).save(entity);

        // Verify cascade delete call to topicService
        verify(topicService).deleteTopic(topicId);
    }
}