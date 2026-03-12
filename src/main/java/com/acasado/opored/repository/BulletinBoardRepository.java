package com.acasado.opored.repository;

import com.acasado.opored.model.BulletinBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulletinBoardRepository extends JpaRepository<BulletinBoardEntity, Integer> {

}
