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
@Table(name = "public_examinations")
@SQLRestriction("is_deleted = false")
public class PublicExaminationEntity {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_board_id")
    private BulletinBoardEntity bulletinBoard;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    private ForumEntity forum;

    @ManyToMany
    @JoinTable(name = "student_public_examinations",
            joinColumns = @JoinColumn(name = "public_examination_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<StudentEntity> students = new LinkedHashSet<>();


    public PublicExaminationEntity(Integer id,String name, String description, CategoryEntity category, BulletinBoardEntity bulletinBoard, ForumEntity forum) {
        setId(id);
        setName(name);
        setDescription(description);
        setCategory(category);
        setBulletinBoard(bulletinBoard);
        setForum(forum);
    }
}
