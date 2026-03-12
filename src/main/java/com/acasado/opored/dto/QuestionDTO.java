package com.acasado.opored.dto;

import com.acasado.opored.model.QuestionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Details of a test question")
public class QuestionDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(description = "The question text", example = "What is the capital of France?")
    @NotBlank
    private String statement;

    @Schema(description = "Order position of the question in the test", example = "1")
    private Byte position;

    @Schema(description = "ID of the associated test", example = "10")
    private Integer testId;

    private Set<AnswerDTO> answers;

    public QuestionDTO(QuestionEntity question) {
        setId(question.getId());
        setStatement(question.getStatement());
        setPosition(question.getPosition());
        if (question.getTest() != null) {
            setTestId(question.getTest().getId());
        }
        setAnswers(question.getAnswers().stream().map(AnswerDTO::new).collect(Collectors.toSet()));
    }
}