package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ratings")
public abstract class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull
    @Column(name = "score", nullable = false)
    private Float score;

    @NotNull
    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate = LocalDate.now();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Lob
    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;
}
