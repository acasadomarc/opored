package com.acasado.opored.repository;

import com.acasado.opored.model.ModerationTopicEntity;
import com.acasado.opored.model.ModerationTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModerationTopicRepository extends JpaRepository<ModerationTopicEntity, ModerationTopicId> {
    Optional<List<ModerationTopicEntity>> findByModeratorId(Integer administratorId);
}
