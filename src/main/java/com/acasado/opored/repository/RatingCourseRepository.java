package com.acasado.opored.repository;

import com.acasado.opored.model.RatingCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingCourseRepository extends JpaRepository<RatingCourseEntity, Integer> {
}
