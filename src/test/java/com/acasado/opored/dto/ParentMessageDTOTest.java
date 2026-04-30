package com.acasado.opored.dto;

import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParentMessageDTOTest {

    @Test
    void When_ConstructorFromEntity_Expect_CorrectMapping() {
        UserEntity user = new StudentEntity();
        user.setId(5);
        user.setAlias("alias");

        TopicEntity topic = new TopicEntity();
        topic.setId(10);

        MessageEntity message = new MessageEntity("Content", StatusEnum.VISIBLE, null, topic, user);
        message.setId(1);

        ParentMessageDTO dto = new ParentMessageDTO(message);

        assertEquals(1, dto.getId());
        assertEquals("Content", dto.getContent());
        assertEquals("VISIBLE", dto.getStatus());
        assertEquals(10, dto.getTopicId());
        assertEquals("alias", dto.getUserSummaryDTO().getAlias());
    }
}
