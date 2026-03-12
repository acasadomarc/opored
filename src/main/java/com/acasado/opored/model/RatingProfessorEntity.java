package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rating_professors")
public class RatingProfessorEntity extends RatingEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private ProfessorEntity professor;

    public RatingProfessorEntity(String title, float score, StudentEntity student, ProfessorEntity professor, String comment) {
        setTitle(title);
        setScore(score);
        setStudent(student);
        setProfessor(professor);
        setComment(comment);
    }
}
