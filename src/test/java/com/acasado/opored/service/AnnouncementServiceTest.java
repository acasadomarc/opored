package com.acasado.opored.service;

import com.acasado.opored.dto.AnnouncementDTO;
import com.acasado.opored.model.AnnouncementEntity;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.repository.AnnouncementRepository;
import com.acasado.opored.repository.BulletinBoardRepository;
import com.acasado.opored.util.AnnouncementFactory;
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
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private BulletinBoardRepository bulletinBoardRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    @Test
    void When_GetAllAnnouncements_Expect_ListDTO() {
        // Arrange
        List<AnnouncementEntity> entities = List.of(AnnouncementFactory.createValidAnnouncementEntity());
        when(announcementRepository.findAll()).thenReturn(entities);

        // Act
        List<AnnouncementDTO> result = announcementService.getAllAnnouncements();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getTitle(), result.getFirst().getTitle());
    }

    @Test
    void When_GetAnnouncementById_Expect_DTO() {
        // Arrange
        AnnouncementEntity entity = AnnouncementFactory.createValidAnnouncementEntity();
        when(announcementRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        AnnouncementDTO result = announcementService.getAnnouncementById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetAnnouncementById_NotFound() {
        // Arrange
        when(announcementRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> announcementService.getAnnouncementById(999));
    }

    @Test
    void When_CreateAnnouncement_Expect_DTO() {
        // Arrange
        AnnouncementDTO inputDto = AnnouncementFactory.createValidAnnouncementDTO();
        AnnouncementEntity savedEntity = AnnouncementFactory.createValidAnnouncementEntity();

        // Mocking the dependency for reference
        when(bulletinBoardRepository.getReferenceById(anyInt())).thenReturn(new BulletinBoardEntity());
        when(announcementRepository.save(any(AnnouncementEntity.class))).thenReturn(savedEntity);

        // Act
        AnnouncementDTO result = announcementService.createAnnouncement(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        verify(announcementRepository).save(any(AnnouncementEntity.class));
    }

    @Test
    void When_UpdateAnnouncement_Expect_UpdatedDTO() {
        // Arrange
        AnnouncementEntity entity = AnnouncementFactory.createValidAnnouncementEntity();
        String newTitle = "Updated Title";
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setTitle(newTitle);
        dto.setContent("Updated Content");
        dto.setRelatedLinks("Links");
        dto.setBulletinBoardId(1);

        when(announcementRepository.findById(1)).thenReturn(Optional.of(entity));
        when(bulletinBoardRepository.getReferenceById(anyInt())).thenReturn(new BulletinBoardEntity());
        when(announcementRepository.save(any(AnnouncementEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        AnnouncementDTO result = announcementService.updateAnnouncement(1,dto);

        // Assert
        assertEquals(newTitle, result.getTitle());
        verify(announcementRepository).save(entity);
    }

    @Test
    void When_DeleteAnnouncement_Expect_LogicalDelete() {
        // Arrange
        AnnouncementEntity entity = AnnouncementFactory.createValidAnnouncementEntity();
        when(announcementRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        announcementService.deleteAnnouncement(1);

        // Assert
        assertTrue(entity.getIsDeleted());
        verify(announcementRepository).save(entity);
    }
}