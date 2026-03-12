package com.acasado.opored.repository;

import com.acasado.opored.model.ModerationMessageEntity;
import com.acasado.opored.model.ModerationMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModerationMessageRepository extends JpaRepository<ModerationMessageEntity, ModerationMessageId> {
    Optional<List<ModerationMessageEntity>> findByModeratorId(Integer administratorId);
}
