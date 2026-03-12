package com.acasado.opored.dto;

import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.MessageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Brief message information")
public class MessageSummaryDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Thank you for the information...")
    private String content;

    @Schema(example = "VISIBLE")
    private StatusEnum status;

    @Schema(example = "2026-07-20")
    private LocalDate publicationDate;

    @Schema(description = "Core info of the parent message")
    private ParentMessageDTO parentMessageDTO;

    @Schema(description = "Core info of the user who wrote the message")
    private UserSummaryDTO userSummaryDTO;

    public MessageSummaryDTO(MessageEntity message) {
        setId(message.getId());
        setContent(message.getContent());
        setStatus(message.getStatus());
        setPublicationDate(message.getPublicationDate());
        if (message.getUser() != null) {
            setUserSummaryDTO(new UserSummaryDTO(message.getUser()));
        }
        if (message.getParentMessage() != null) {
            setParentMessageDTO(new ParentMessageDTO(message.getParentMessage()));
        }
    }
}