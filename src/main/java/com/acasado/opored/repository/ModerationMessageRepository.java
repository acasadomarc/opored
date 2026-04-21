package com.acasado.opored.repository;

import com.acasado.opored.model.ModerationMessageEntity;
import com.acasado.opored.model.ModerationMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationMessageRepository extends JpaRepository<ModerationMessageEntity, ModerationMessageId> {
}
