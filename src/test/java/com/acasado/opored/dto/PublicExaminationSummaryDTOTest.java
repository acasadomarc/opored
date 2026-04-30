package com.acasado.opored.dto;

import com.acasado.opored.model.BulletinBoardEntity;
import com.acasado.opored.model.ForumEntity;
import com.acasado.opored.model.PublicExaminationEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicExaminationSummaryDTOTest {

    @Test
    void When_ConstructorFromEntity_Expect_CorrectMapping() {
        PublicExaminationEntity entity = new PublicExaminationEntity();
        entity.setId(1);
        entity.setName("Name");
        entity.setDescription("Desc");
        entity.setVisible(true);

        BulletinBoardEntity board = new BulletinBoardEntity();
        board.setId(10);
        entity.setBulletinBoard(board);

        ForumEntity forum = new ForumEntity();
        forum.setId(20);
        entity.setForum(forum);

        PublicExaminationSummaryDTO dto = new PublicExaminationSummaryDTO(entity);

        assertEquals(1, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals(10, dto.getBulletinBoardId());
        assertEquals(20, dto.getForumId());
    }
}
