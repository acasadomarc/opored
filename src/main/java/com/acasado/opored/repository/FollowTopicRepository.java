package com.acasado.opored.repository;

import com.acasado.opored.model.FollowTopic;
import com.acasado.opored.model.FollowTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowTopicRepository extends JpaRepository<FollowTopic, FollowTopicId> {
}
