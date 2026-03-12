package com.acasado.opored.dto;

import com.acasado.opored.model.AnswerEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Details of an answer option for a test question")
public class AnswerDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(description = "Text of the answer option", example = "The legislative power resides in the Parliament.")
    @NotNull
    private String reply;

    @Schema(description = "Indicates if this option is the correct answer", example = "true")
    private Boolean isCorrect;

    @Schema(description = "ID of the associated question", example = "15")
    private Integer questionId;

    public AnswerDTO(AnswerEntity answer) {
        setId(answer.getId());
        setReply(answer.getReply());
        setIsCorrect(answer.getIsCorrect());
        if (answer.getQuestion() != null) {
            setQuestionId(answer.getQuestion().getId());
        }
    }
}