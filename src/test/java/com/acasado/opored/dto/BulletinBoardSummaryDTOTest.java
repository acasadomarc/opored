package com.acasado.opored.dto;

import com.acasado.opored.model.BulletinBoardEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BulletinBoardSummaryDTOTest {

    @Test
    void When_ConstructorFromEntity_Expect_CorrectMapping() {
        BulletinBoardEntity entity = new BulletinBoardEntity(1, "Board Name", "Description");
        BulletinBoardSummaryDTO dto = new BulletinBoardSummaryDTO(entity);

        assertEquals(1, dto.getId());
        assertEquals("Board Name", dto.getName());
    }
}
