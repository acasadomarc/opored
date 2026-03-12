package com.acasado.opored.util;

import com.acasado.opored.dto.AnnouncementDTO;
import com.acasado.opored.model.AnnouncementEntity;
import com.acasado.opored.model.BulletinBoardEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AnnouncementFactory {

    public static AnnouncementDTO createValidAnnouncementDTO() {
        return new AnnouncementDTO(
                1,
                "New Exams",
                "Registration opens tomorrow.",
                "http://link.com",
                LocalDate.now(),
                10 // BulletinBoard ID
        );
    }

    public static AnnouncementDTO createInvalidAnnouncementDTO() {
        // Validation: Assuming Title or Content cannot be null (based on Controller @NotNull @Valid)
        return new AnnouncementDTO(
                null,
                null, // Invalid null title
                null,
                null,
                null,
                10
        );
    }

    public static AnnouncementEntity createValidAnnouncementEntity() {
        BulletinBoardEntity bulletinBoard = new BulletinBoardEntity();
        bulletinBoard.setId(10);

        AnnouncementEntity entity = new AnnouncementEntity();
        entity.setId(1);
        entity.setTitle("New Exams");
        entity.setContent("Registration opens tomorrow.");
        entity.setRelatedLinks("http://link.com");
        entity.setPublicationDate(LocalDate.now());
        entity.setBulletinBoard(bulletinBoard);
        entity.setIsDeleted(false);
        return entity;
    }
}