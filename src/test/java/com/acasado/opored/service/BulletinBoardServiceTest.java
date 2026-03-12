package com.acasado.opored.service;

import com.acasado.opored.dto.BulletinBoardDTO;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.repository.BulletinBoardRepository;
import com.acasado.opored.util.BulletinBoardFactory;
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
class BulletinBoardServiceTest {

    @Mock
    private BulletinBoardRepository bulletinBoardRepository;

    @Mock
    private AnnouncementService announcementService;

    @InjectMocks
    private BulletinBoardService bulletinBoardService;

    @Test
    void When_GetAllBulletinBoards_Expect_ListDTO() {
        // Arrange
        List<BulletinBoardEntity> entities = List.of(BulletinBoardFactory.createValidBulletinBoardEntity());
        when(bulletinBoardRepository.findAll()).thenReturn(entities);

        // Act
        List<BulletinBoardDTO> result = bulletinBoardService.getAllBulletinBoards();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(entities.getFirst().getName(), result.getFirst().getName());
    }

    @Test
    void When_GetBulletinBoardById_Expect_DTO() {
        // Arrange
        BulletinBoardEntity entity = BulletinBoardFactory.createValidBulletinBoardEntity();
        when(bulletinBoardRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        BulletinBoardDTO result = bulletinBoardService.getBulletinBoardById(1);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }

    @Test
    void Expect_Exception_When_GetBulletinBoardById_NotFound() {
        // Arrange
        when(bulletinBoardRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bulletinBoardService.getBulletinBoardById(999));
    }

    @Test
    void When_CreateBulletinBoard_Expect_Entity() {
        // Arrange
        BulletinBoardEntity inputEntity = BulletinBoardFactory.createValidBulletinBoardEntity();
        when(bulletinBoardRepository.save(any(BulletinBoardEntity.class))).thenReturn(inputEntity);

        // Act
        BulletinBoardEntity result = bulletinBoardService.createBulletinBoard(inputEntity);

        // Assert
        assertNotNull(result);
        assertEquals(inputEntity.getName(), result.getName());
    }

    @Test
    void When_UpdateBulletinBoard_Expect_UpdatedDTO() {
        // Arrange
        BulletinBoardEntity entity = BulletinBoardFactory.createValidBulletinBoardEntity();
        String newName = "New Name";

        when(bulletinBoardRepository.findById(1)).thenReturn(Optional.of(entity));
        when(bulletinBoardRepository.save(any(BulletinBoardEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        BulletinBoardDTO result = bulletinBoardService.updateBulletinBoard(1, newName, "New Desc");

        // Assert
        assertEquals(newName, result.getName());
        verify(bulletinBoardRepository).save(entity);
    }

    @Test
    void When_DeleteBulletinBoard_Expect_LogicalDeleteAndCascade() {
        // Arrange
        // Create an entity that has announcements to test the loop
        BulletinBoardEntity entity = BulletinBoardFactory.createBulletinBoardEntityWithAnnouncements();
        int announcementId = entity.getAnnouncements().iterator().next().getId();

        when(bulletinBoardRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        bulletinBoardService.deleteBulletinBoard(1);

        // Assert
        // 1. Verify logical delete of the board
        assertTrue(entity.getIsDeleted());
        verify(bulletinBoardRepository).save(entity);

        // 2. Verify cascade delete call to announcementService
        verify(announcementService).deleteAnnouncement(announcementId);
    }
}