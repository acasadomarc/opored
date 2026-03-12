package com.acasado.opored.repository;

import com.acasado.opored.model.ForumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<ForumEntity, Integer> {
}
