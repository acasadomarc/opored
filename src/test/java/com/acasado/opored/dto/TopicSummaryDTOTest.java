package com.acasado.opored.dto;

import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.StudentEntity;
import com.acasado.opored.model.TopicEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicSummaryDTOTest {

    @Test
    void When_ConstructorFromEntity_Expect_CorrectMapping() {
        ForumEntity forum = new ForumEntity();
        forum.setId(10);
        StudentEntity user = new StudentEntity();
        user.setId(5);

        TopicEntity entity = new TopicEntity("Title", StatusEnum.VISIBLE, forum, user);
        entity.setId(1);
        entity.setPublicationDate(LocalDate.now());

        TopicSummaryDTO dto = new TopicSummaryDTO(entity);

        assertEquals(1, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals(StatusEnum.VISIBLE, dto.getStatus());
        assertEquals(10, dto.getForumId());
        assertEquals(5, dto.getUserId());
    }
}
