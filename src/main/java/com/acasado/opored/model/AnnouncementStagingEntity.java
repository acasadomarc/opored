package com.acasado.opored.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "announcements_staging")
public class AnnouncementStagingEntity {

    @EmbeddedId
    private AnnouncementStagingId id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Lob
    @Column(name = "related_links", columnDefinition = "text")
    private String relatedLinks;

    @Column(name = "classification_confidence")
    private double classificationConfidence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bulletin_board_id", nullable = false)
    private BulletinBoardEntity bulletinBoard;

    public AnnouncementStagingEntity(String title, String content, double confidence, BulletinBoardEntity bulletinBoard) {
        setId(new AnnouncementStagingId(title, LocalDate.now()));
        setContent(content);
        setClassificationConfidence(confidence);
        this.relatedLinks = null;
        setBulletinBoard(bulletinBoard);
    }
}