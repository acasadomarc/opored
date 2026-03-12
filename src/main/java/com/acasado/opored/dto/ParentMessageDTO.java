package com.acasado.opored.dto;

import com.acasado.opored.model.MessageEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParentMessageDTO {
    private Integer id;
    private String content;
    private Integer topicId;
    private UserSummaryDTO userSummaryDTO;

    public ParentMessageDTO(MessageEntity message) {
        setId(message.getId());
        setContent(message.getContent());
        setTopicId(message.getTopic().getId());
        setUserSummaryDTO(new UserSummaryDTO(message.getUser()));
    }

}
