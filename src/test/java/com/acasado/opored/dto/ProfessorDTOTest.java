package com.acasado.opored.dto;

import com.acasado.opored.model.RatingProfessorEntity;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfessorDTOTest {

    @Test
    void When_SetRatings_Expect_DeletedFilteredAndScoreCalculated() {
        ProfessorDTO dto = new ProfessorDTO();
        
        RatingProfessorEntity r1 = new RatingProfessorEntity();
        r1.setScore(5.0f);
        r1.setDeleted(false);
        r1.setTitle("T1");

        RatingProfessorEntity r2 = new RatingProfessorEntity();
        r2.setScore(1.0f);
        r2.setDeleted(true);
        r2.setTitle("T2");

        Set<RatingProfessorEntity> ratings = new LinkedHashSet<>();
        ratings.add(r1);
        ratings.add(r2);

        dto.setRatings(ratings);
        dto.setTotalScore();

        assertEquals(1, dto.getRatings().size());
        assertEquals(5.0f, dto.getTotalScore());
    }

    @Test
    void When_SetRatingsEmpty_Expect_ZeroScore() {
        ProfessorDTO dto = new ProfessorDTO();
        dto.setRatings(new LinkedHashSet<>());
        dto.setTotalScore();
        assertEquals(0.0f, dto.getTotalScore());
    }
}
