package com.acasado.opored.service;

import com.acasado.opored.dto.AnnouncementDTO;
import com.acasado.opored.model.AnnouncementEntity;
import com.acasado.opored.repository.AnnouncementRepository;
import com.acasado.opored.repository.BulletinBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final BulletinBoardRepository bulletinBoardRepository;

    @Value("${unassigned.announcement.examination.id}")
    private Integer unassignedAnnouncementExaminationId;

    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream().map(this::convertToAnnouncementDTO).toList();
    }

    public List<AnnouncementDTO> getUncategorizedAnnouncements() {
        return announcementRepository.findByBulletinBoard_Id(unassignedAnnouncementExaminationId).stream().map(this::convertToAnnouncementDTO).toList();
    }

    public AnnouncementDTO getAnnouncementById(Integer id) {
        AnnouncementEntity announcement = announcementRepository.findById(id).orElseThrow(() -> notFoundById(id));
        return convertToAnnouncementDTO(announcement);
    }

    public AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO) {
        AnnouncementEntity announcement = convertToAnnouncement(announcementDTO);
        AnnouncementEntity savedAnnouncement = announcementRepository.save(announcement);
        return convertToAnnouncementDTO(savedAnnouncement);
    }

    public AnnouncementDTO updateAnnouncement(Integer id, AnnouncementDTO announcementDTO) {
        AnnouncementEntity toUpdateAnnouncement = announcementRepository.findById(id).orElseThrow(() -> notFoundById(id));

        toUpdateAnnouncement.setTitle(announcementDTO.getTitle());
        toUpdateAnnouncement.setContent(announcementDTO.getContent());
        toUpdateAnnouncement.setRelatedLinks(announcementDTO.getRelatedLinks());
        toUpdateAnnouncement.setBulletinBoard(bulletinBoardRepository.getReferenceById(announcementDTO.getBulletinBoardId()));

        AnnouncementEntity updatedAnnouncement = announcementRepository.save(toUpdateAnnouncement);
        return convertToAnnouncementDTO(updatedAnnouncement);
    }

    public void deleteAnnouncement(Integer id) {
        AnnouncementEntity toDeleteAnnouncement = announcementRepository.findById(id)
                .orElseThrow(() -> notFoundById(id));

        // Logical delete
        toDeleteAnnouncement.setIsDeleted(true);
        announcementRepository.save(toDeleteAnnouncement);
    }

    private AnnouncementDTO convertToAnnouncementDTO(AnnouncementEntity announcement) {
        return new AnnouncementDTO(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getRelatedLinks(),
                announcement.getPublicationDate(),
                announcement.getBulletinBoard().getId());
    }

    private AnnouncementEntity convertToAnnouncement(AnnouncementDTO announcementDTO) {
        return new AnnouncementEntity(
                announcementDTO.getTitle(),
                announcementDTO.getContent(),
                announcementDTO.getRelatedLinks(),
                announcementDTO.getPublicationDate(),
                bulletinBoardRepository.getReferenceById(announcementDTO.getBulletinBoardId()));
    }

    private EntityNotFoundException notFoundById(Integer id) {
        return new EntityNotFoundException(String.format("Announcement with id %d not found", id));
    }
}
