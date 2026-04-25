package com.acasado.opored.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ContentDTOTest {

    @Test
    void When_FromEntityNull_Expect_Null() {
        assertNull(ContentDTO.fromEntity(null));
    }
}
