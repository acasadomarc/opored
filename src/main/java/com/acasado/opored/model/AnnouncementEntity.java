package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "announcements")
@SQLRestriction("is_deleted = false")
public class AnnouncementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Lob
    @Column(name = "related_links", columnDefinition = "text")
    private String relatedLinks;

    @CreationTimestamp
    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bulletin_board_id", nullable = false)
    private BulletinBoardEntity bulletinBoard;

    public AnnouncementEntity(String title, String content, String relatedLinks, LocalDate publicationDate, BulletinBoardEntity bulletinBoard) {
        setTitle(title);
        setContent(content);
        setRelatedLinks(relatedLinks);
        setPublicationDate(publicationDate);
        setBulletinBoard(bulletinBoard);
    }
}
