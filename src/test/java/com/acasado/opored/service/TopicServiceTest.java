package com.acasado.opored.service;

import com.acasado.opored.dto.TopicDTO;
import com.acasado.opored.dto.StudentSummaryDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.exception.UserWithoutPermissionException;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import com.acasado.opored.repository.ForumRepository;
import com.acasado.opored.repository.StudentRepository;
import com.acasado.opored.repository.TopicRepository;
import com.acasado.opored.repository.UserRepository;
import com.acasado.opored.security.SecurityUtils;
import com.acasado.opored.util.TopicFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock private TopicRepository topicRepository;
    @Mock private ForumRepository forumRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private UserRepository userRepository;
    @Mock private StudentService studentService;
    @Mock private MessageService messageService;

    @InjectMocks
    private TopicService topicService;

    // --- GetAll ---
    @Test
    void When_GetAllTopics_Expect_List() {
        when(topicRepository.findAll()).thenReturn(List.of(TopicFactory.createValidTopicEntity()));
        List<TopicDTO> result = topicService.getAllTopics();
        assertFalse(result.isEmpty());
    }

    // --- GetById ---
    @Test
    void When_GetById_Expect_DTO() {
        TopicEntity entity = TopicFactory.createValidTopicEntity();
        when(topicRepository.findById(1)).thenReturn(Optional.of(entity));

        TopicDTO result = topicService.getTopicById(1);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    // --- Create ---
    @Test
    void When_CreateTopic_AsStudent_Expect_AutoFollow() {
        // Arrange
        TopicDTO inputDto = TopicFactory.createValidTopicDTO();
        TopicEntity savedEntity = TopicFactory.createValidTopicEntity();

        when(forumRepository.existsById(inputDto.getForumId())).thenReturn(true);
        when(forumRepository.getReferenceById(anyInt())).thenReturn(new ForumEntity());
        when(userRepository.getReferenceById(anyInt())).thenReturn(new StudentEntity());
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new StudentEntity()));

        when(topicRepository.save(any(TopicEntity.class))).thenReturn(savedEntity);

        // Mock that the user IS a student
        // Act
        TopicDTO result = topicService.createTopic(inputDto);

        // Assert
        assertNotNull(result);
        verify(topicRepository).save(any(TopicEntity.class));
        // Verify auto-follow logic
        verify(studentService).followTopic(savedEntity.getId());
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateMyTopic_Owner_Expect_Success() {
        // Arrange
        int userId = 5;
        TopicEntity entity = TopicFactory.createValidTopicEntity();
        entity.getUser().setId(userId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            when(topicRepository.findById(1)).thenReturn(Optional.of(entity));
            when(topicRepository.save(any(TopicEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            TopicDTO result = topicService.updateMyTopic(1, "New Title", "HIDDEN");

            // Assert
            assertEquals("New Title", result.getTitle());
            assertEquals(StatusEnum.HIDDEN, StatusEnum.valueOf(result.getStatus()));
        }
    }

    @Test
    void Expect_Exception_When_UpdateMyTopic_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        TopicEntity entity = TopicFactory.createValidTopicEntity();
        entity.getUser().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(topicRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(UserWithoutPermissionException.class, () ->
                    topicService.updateMyTopic(1, "Title", "VISIBLE"));
        }
    }

    // --- Delete (Cascade) ---
    @Test
    void When_DeleteTopic_Expect_CascadeDeleteAndUnfollow() {
        // Arrange
        TopicEntity entity = TopicFactory.createTopicWithMessagesAndFollowers();
        int messageId = entity.getMessages().iterator().next().getId();
        int studentId = entity.getStudentsFollowing().iterator().next().getId();

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization (e.g., as Root)
            securityMock.when(SecurityUtils::isUserAdmin).thenReturn(true);

            when(topicRepository.findById(1)).thenReturn(Optional.of(entity));
            // Student exists check for unfollow logic
            when(studentRepository.existsById(anyInt())).thenReturn(true);

            // Act
            topicService.deleteTopic(1);

            // Assert
            assertTrue(entity.getIsDeleted());
            verify(topicRepository).save(entity);

            // Verify cascade calls
            verify(messageService).deleteMessage(messageId);
            verify(studentService).unfollowDeletedTopic(studentId, 1);
        }
    }

    // --- Get Students Following ---
    @Test
    void When_GetStudentsFollowing_Expect_Set() {
        TopicEntity entity = TopicFactory.createTopicWithMessagesAndFollowers();
        when(topicRepository.findById(1)).thenReturn(Optional.of(entity));

        Set<StudentSummaryDTO> result = topicService.getStudentsFollowing(1);
        assertFalse(result.isEmpty());
    }
}