package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "quizzes")
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("QUIZ")
public class QuizEntity extends ContentEntity {
    @Column(name = "allowed_attempts")
    private Integer allowedAttempts;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "score_to_pass")
    private Integer scoreToPass;

    @NotNull
    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @OneToMany(mappedBy = "test")
    private Set<QuestionEntity> questions = new LinkedHashSet<>();

    public QuizEntity(String title, String description, Integer allowedAttempts, Integer timeLimit, Integer scoreToPass, Integer maxScore, Set<QuestionEntity> questions) {
        setTitle(title);
        setDescription(description);
        setAllowedAttempts(allowedAttempts);
        setTimeLimit(timeLimit);
        setScoreToPass(scoreToPass);
        setMaxScore(maxScore);
        setQuestions(questions);
    }

}
