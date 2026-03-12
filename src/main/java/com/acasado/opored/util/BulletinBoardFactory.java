package com.acasado.opored.util;

import com.acasado.opored.dto.BulletinBoardDTO;
import com.acasado.opored.model.AnnouncementEntity;
import com.acasado.opored.model.BulletinBoardEntity;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BulletinBoardFactory {

    public static BulletinBoardDTO createValidBulletinBoardDTO() {
        return new BulletinBoardDTO(
                1,
                "General Board",
                "General information",
                new HashSet<>() // Empty set for DTO simplicity in basic tests
        );
    }

    public static BulletinBoardEntity createValidBulletinBoardEntity() {
        BulletinBoardEntity entity = new BulletinBoardEntity();
        entity.setId(1);
        entity.setName("General Board");
        entity.setDescription("General information");
        entity.setIsDeleted(false);
        entity.setAnnouncements(new HashSet<>());
        return entity;
    }

    // Helper to create an entity with announcements for delete cascade testing
    public static BulletinBoardEntity createBulletinBoardEntityWithAnnouncements() {
        BulletinBoardEntity entity = createValidBulletinBoardEntity();

        AnnouncementEntity announcement = new AnnouncementEntity();
        announcement.setId(100);
        announcement.setTitle("Test Announcement");

        Set<AnnouncementEntity> announcements = new HashSet<>();
        announcements.add(announcement);

        entity.setAnnouncements(announcements);
        return entity;
    }
}