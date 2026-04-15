package com.acasado.opored.repository;

import com.acasado.opored.model.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Integer> {
    List<AnnouncementEntity> findByBulletinBoard_Id(Integer bulletinBoardId);
}
