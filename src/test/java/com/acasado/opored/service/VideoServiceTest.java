package com.acasado.opored.service;

import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.exception.ProfessorWithoutPermissionException;
import com.acasado.opored.model.CourseEntity;
import com.acasado.opored.model.ProfessorEntity;
import com.acasado.opored.model.VideoEntity;
import com.acasado.opored.repository.CourseRepository;
import com.acasado.opored.repository.VideoRepository;
import com.acasado.opored.util.SecurityUtils;
import com.acasado.opored.util.VideoFactory;
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
class VideoServiceTest {

    @Mock private VideoRepository videoRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private VideoService videoService;

    // --- GetAll ---
    @Test
    void When_GetAllVideos_Expect_List() {
        when(videoRepository.findAll()).thenReturn(List.of(VideoFactory.createValidVideoEntity()));
        List<VideoDTO> result = videoService.getAllVideos();
        assertFalse(result.isEmpty());
    }

    // --- Create (Security) ---
    @Test
    void When_CreateVideo_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        VideoDTO inputDto = VideoFactory.createValidVideoDTO();
        CourseEntity course = new CourseEntity();
        course.setId(inputDto.getCourseId());
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));
            when(videoRepository.save(any(VideoEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            VideoDTO result = videoService.createVideo(inputDto);

            // Assert
            assertNotNull(result);
            verify(videoRepository).save(any(VideoEntity.class));
        }
    }

    @Test
    void Expect_Exception_When_CreateVideo_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        VideoDTO inputDto = VideoFactory.createValidVideoDTO();
        CourseEntity course = new CourseEntity();
        course.setProfessor(new ProfessorEntity());
        course.getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(courseRepository.findById(inputDto.getCourseId())).thenReturn(Optional.of(course));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> videoService.createVideo(inputDto));
            verify(videoRepository, never()).save(any());
        }
    }

    // --- Update (Security) ---
    @Test
    void When_UpdateVideo_Owner_Expect_Success() {
        // Arrange
        int professorId = 5;
        VideoDTO updateDto = VideoFactory.createValidVideoDTO();
        updateDto.setTitle("New Title");

        VideoEntity entity = VideoFactory.createValidVideoEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(professorId);

            when(videoRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));
            when(videoRepository.save(any(VideoEntity.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            VideoDTO result = videoService.updateVideo(updateDto);

            // Assert
            assertEquals("New Title", result.getTitle());
        }
    }

    @Test
    void Expect_Exception_When_UpdateVideo_NotOwner() {
        // Arrange
        int ownerId = 5;
        int intruderId = 99;
        VideoDTO updateDto = VideoFactory.createValidVideoDTO();
        VideoEntity entity = VideoFactory.createValidVideoEntity();
        entity.getCourse().getProfessor().setId(ownerId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(intruderId);
            when(videoRepository.findById(updateDto.getId())).thenReturn(Optional.of(entity));

            // Act & Assert
            assertThrows(ProfessorWithoutPermissionException.class, () -> videoService.updateVideo(updateDto));
        }
    }

    // --- Delete (Security) ---
    @Test
    void When_DeleteVideo_Owner_Expect_LogicalDelete() {
        // Arrange
        int professorId = 5;
        VideoEntity entity = VideoFactory.createValidVideoEntity();
        entity.getCourse().getProfessor().setId(professorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Mock authorization logic used inside isAuthorized()
            securityMock.when(() -> SecurityUtils.isProvidedUser(professorId)).thenReturn(true);

            when(videoRepository.findById(1)).thenReturn(Optional.of(entity));

            // Act
            videoService.deleteVideo(1);

            // Assert
            assertTrue(entity.getIsDeleted());
            verify(videoRepository).save(entity);
        }
    }

    @Test
    void Expect_Exception_When_DeleteVideo_NotFound() {
        when(videoRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> videoService.deleteVideo(999));
    }
}