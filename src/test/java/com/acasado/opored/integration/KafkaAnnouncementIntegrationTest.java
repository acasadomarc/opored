package com.acasado.opored.integration;

import com.acasado.opored.dto.kafka.BoeAnnouncementDTO;
import com.acasado.opored.integration.base.BaseIntegrationTest;
import com.acasado.opored.model.AnnouncementClassificationKeywords;
import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.model.CategoryEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import com.acasado.opored.repository.*;
import com.acasado.opored.service.AnnouncementAssignmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaAnnouncementIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AnnouncementAssignmentService announcementAssignmentService;

    @Autowired
    private PublicExaminationRepository publicExaminationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BulletinBoardRepository bulletinBoardRepository;

    @Autowired
    private AnnouncementStagingRepository announcementStagingRepository;

    @Autowired
    private AnnouncementClassificationKeywordsRepository announcementClassificationKeywordsRepository;

    @Test
    void When_KafkaAnnouncementMatchesTags_Expect_AssignedToMatchingBoard() {
        // Arrange
        CategoryEntity category = categoryRepository.save(new CategoryEntity("Cat", "Desc"));
        BulletinBoardEntity bulletinBoard = bulletinBoardRepository.save(new BulletinBoardEntity("Board", "Desc"));
        PublicExaminationEntity exam = new PublicExaminationEntity("Exam", "Desc", category, bulletinBoard, null);
        AnnouncementClassificationKeywords keywords = new AnnouncementClassificationKeywords();
        keywords.setMainTags("math,exam,notice");
        keywords.setSecondaryTags("");
        keywords.setExclusionTags("");
        keywords.setPublicExamination(exam);
        publicExaminationRepository.save(exam);
        announcementClassificationKeywordsRepository.save(keywords);

        BoeAnnouncementDTO announcement = new BoeAnnouncementDTO(
                "Math Exam Notice",
                "http://html",
                "http://pdf",
                "ID-1",
                LocalDate.now()
        );

        // Act
        announcementAssignmentService.kafkaAnnouncementCategorization(announcement);

        // Assert
        var staging = announcementStagingRepository.findAll();
        assertEquals(1, staging.size());
        assertEquals(bulletinBoard.getId(), staging.getFirst().getBulletinBoard().getId());
    }

    @Test
    void When_KafkaAnnouncementDoesNotMatch_Expect_FallbackExam() {
        // Arrange
        CategoryEntity category = categoryRepository.save(new CategoryEntity("Cat2", "Desc2"));
        BulletinBoardEntity fallbackBoard = null;
        for (int i = 0; i < 8; i++) {
            BulletinBoardEntity board = bulletinBoardRepository.save(new BulletinBoardEntity("Board-" + i, "Desc"));
            PublicExaminationEntity exam = new PublicExaminationEntity("Exam-" + i, "Desc", category, board, null);
            exam = publicExaminationRepository.save(exam);
            if (exam.getId() == 9) {
                fallbackBoard = board;
            }
        }

        assertNotNull(fallbackBoard);

        BoeAnnouncementDTO announcement = new BoeAnnouncementDTO(
                "Unmatched Notice",
                "http://html",
                "http://pdf",
                "ID-2",
                LocalDate.now()
        );

        // Act
        announcementAssignmentService.kafkaAnnouncementCategorization(announcement);

        // Assert
        var staging = announcementStagingRepository.findAll();
        assertEquals(1, staging.size());
        assertEquals(fallbackBoard.getId(), staging.getFirst().getBulletinBoard().getId());
    }
}