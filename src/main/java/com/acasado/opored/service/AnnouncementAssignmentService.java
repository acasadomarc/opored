package com.acasado.opored.service;

import com.acasado.opored.dto.kafka.ClassificationResult;
import com.acasado.opored.dto.kafka.KafkaAnnouncementDTO;
import com.acasado.opored.model.AnnouncementStagingEntity;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import com.acasado.opored.repository.AnnouncementStagingRepository;
import com.acasado.opored.repository.PublicExaminationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementAssignmentService {

    private final PublicExaminationRepository publicExaminationRepository;
    private final AnnouncementStagingRepository announcementStagingRepository;
    private final AnnouncementScoringClassifierService scoringClassifierService;

    @Value("${unassigned.announcement.examination.id}")
    private Integer unassignedAnnouncementExaminationId;

    public void kafkaAnnouncementCategorization(KafkaAnnouncementDTO kafkaAnnouncementDTO) {
        // Descartamos directamente los anuncios con un título genérico referidos a convocatorias de plazas en
        // ayuntamientos de municipios o diputaciones
        if (kafkaAnnouncementDTO.getTitle().contains("referente a la convocatoria para proveer")) {
            return;
        }

        ClassificationResult result = scoringClassifierService.classify(kafkaAnnouncementDTO);

        if (result.isClassified()) {
            BulletinBoardEntity bulletinBoard = result.getPublicExamination().getBulletinBoard();
            AnnouncementStagingEntity announcement = kafkaAnnouncementToAnnouncement(kafkaAnnouncementDTO, bulletinBoard, result.getConfidence());
            // Max varchar size
            if (announcement.getId().getTitle().length() <= 767) {
                announcementStagingRepository.save(announcement);
            }

        }
        else {
            // En caso de que no coincida con ninguna de las oposiciones existentes, se añade a una oposición genérica, que tiene id 8
            PublicExaminationEntity publicExamination = publicExaminationRepository.findById(unassignedAnnouncementExaminationId).orElseThrow(EntityNotFoundException::new);
            BulletinBoardEntity bulletinBoard = publicExamination.getBulletinBoard();
            AnnouncementStagingEntity announcement = kafkaAnnouncementToAnnouncement(kafkaAnnouncementDTO, bulletinBoard, result.getConfidence());
            announcementStagingRepository.save(announcement);
        }
    }

    private AnnouncementStagingEntity kafkaAnnouncementToAnnouncement(KafkaAnnouncementDTO kafkaAnnouncementDTO, BulletinBoardEntity bulletinBoard, double confidence) {
        return new AnnouncementStagingEntity(
                kafkaAnnouncementDTO.getTitle(),
                "Enlace al contenido en formato PDF: " + kafkaAnnouncementDTO.getPdfUrl() + "\nEnlace al contenido en formato HTML: " + kafkaAnnouncementDTO.getHtmlUrl(),
                confidence,
                bulletinBoard);
    }
}
