package com.acasado.opored.repository;

import com.acasado.opored.model.ModerationTopicEntity;
import com.acasado.opored.model.ModerationTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationTopicRepository extends JpaRepository<ModerationTopicEntity, ModerationTopicId> {
}
