package com.acasado.opored.repository;

import com.acasado.opored.model.PublicExaminationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicExaminationRepository extends JpaRepository<PublicExaminationEntity, Integer> {

}
