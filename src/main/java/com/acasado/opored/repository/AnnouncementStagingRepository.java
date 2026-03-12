package com.acasado.opored.repository;

import com.acasado.opored.model.AnnouncementStagingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementStagingRepository extends JpaRepository<AnnouncementStagingEntity, Integer> {
}
