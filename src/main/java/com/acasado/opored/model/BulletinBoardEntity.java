package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bulletin_boards")
@SQLRestriction("is_deleted = false")
public class BulletinBoardEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "bulletinBoard")
    private Set<AnnouncementEntity> announcements = new LinkedHashSet<>();

    public BulletinBoardEntity(Integer id, String name, String description) {
        setId(id);
        setName(name);
        setDescription(description);
    }
}
