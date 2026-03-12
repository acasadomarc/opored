package com.acasado.opored.model;

import com.acasado.opored.enumeration.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;


import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "topics")
@SQLRestriction("is_deleted = false")
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @CreationTimestamp
    @Column(name = "publication_date", updatable = false)
    private LocalDate publicationDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forum_id", nullable = false)
    private ForumEntity forum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "topic")
    private Set<MessageEntity> messages = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "topicsFollowed")
    private Set<StudentEntity> studentsFollowing = new LinkedHashSet<>();


    public TopicEntity(String title, StatusEnum status, ForumEntity forum, UserEntity user) {
        setTitle(title);
        setStatus(status);
        setForum(forum);
        setUser(user);
    }
}
