package com.acasado.opored.dto;

import com.acasado.opored.model.QuizEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Quiz definition details")
public class QuizDTO extends ContentDTO {
    @Schema(description = "Time limit in minutes", example = "60")
    private Integer timeLimit;

    @Schema(description = "Minimum score required to pass", example = "50")
    private Integer scoreToPass;

    @Schema(description = "Maximum possible score", example = "100")
    private Integer maxScore;

    private Set<QuestionDTO> questions;

    public QuizDTO(QuizEntity quizEntity) {
        setId(quizEntity.getId());
        setTitle(quizEntity.getTitle());
        setDescription(quizEntity.getDescription());
        setTimeLimit(quizEntity.getTimeLimit());
        setScoreToPass(quizEntity.getScoreToPass());
        setMaxScore(quizEntity.getMaxScore());
        if (quizEntity.getCourse() != null) {
            setCourseId(quizEntity.getCourse().getId());
        }
        setQuestions(quizEntity.getQuestions().stream().map(QuestionDTO::new).collect(Collectors.toSet()));
    }

    public QuizDTO(Integer id, String title, String description, Integer timeLimit, Integer scoreToPass, Integer maxScore, Integer courseId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setTimeLimit(timeLimit);
        setScoreToPass(scoreToPass);
        setMaxScore(maxScore);
        setCourseId(courseId);
    }
}