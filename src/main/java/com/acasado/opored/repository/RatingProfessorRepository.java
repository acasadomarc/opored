package com.acasado.opored.repository;

import com.acasado.opored.model.RatingProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingProfessorRepository extends JpaRepository<RatingProfessorEntity, Integer> {
    Optional<RatingProfessorEntity> findByStudentId(Integer ratingId);
}
