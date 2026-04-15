package com.acasado.opored.service;

import com.acasado.opored.dto.BulletinBoardDTO;
import com.acasado.opored.dto.BulletinBoardSummaryDTO;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.repository.BulletinBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BulletinBoardService {

    private final BulletinBoardRepository bulletinBoardRepository;
    private final AnnouncementService announcementService;

    public List<BulletinBoardDTO> getAllBulletinBoards() {
        return bulletinBoardRepository.findAll().stream().map(this::convertToBulletinBoardDTO).toList();
    }

    public List<BulletinBoardSummaryDTO> getAllBulletinBoardsSummarized() {
        return bulletinBoardRepository.findAll().stream().map(BulletinBoardSummaryDTO::new).toList();
    }

    public BulletinBoardDTO getBulletinBoardById(Integer id) {
        BulletinBoardEntity bulletinBoard = bulletinBoardRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToBulletinBoardDTO(bulletinBoard);
    }

    public BulletinBoardEntity createBulletinBoard(BulletinBoardEntity bulletinBoard) {
        return bulletinBoardRepository.save(bulletinBoard);
    }

    public BulletinBoardDTO updateBulletinBoard(Integer id, String name, String description) {
        BulletinBoardEntity toUpdateBulletinBoard = bulletinBoardRepository.findById(id).orElseThrow(() -> notFoundById(id));

        toUpdateBulletinBoard.setName(name);
        toUpdateBulletinBoard.setDescription(description);

        BulletinBoardEntity updatedBulletinBoard = bulletinBoardRepository.save(toUpdateBulletinBoard);
        return convertToBulletinBoardDTO(updatedBulletinBoard);
    }

    public void deleteBulletinBoard(Integer id) {
        BulletinBoardEntity toDeleteBulletinBoard = bulletinBoardRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteBulletinBoard.setIsDeleted(true);
        bulletinBoardRepository.save(toDeleteBulletinBoard);

        // Announcements are forced to be deleted at the same time as their bulletin board
        toDeleteBulletinBoard.getAnnouncements().forEach(announcement -> announcementService.deleteAnnouncement(announcement.getId()));
    }

    private BulletinBoardDTO convertToBulletinBoardDTO(BulletinBoardEntity bulletinBoard) {
        return new BulletinBoardDTO(bulletinBoard);
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Bulletin board with id %d not found", id));
    }
}
