package com.acasado.opored.repository;

import com.acasado.opored.model.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Integer> {
    Optional<ProfessorEntity> findByEmail(String email);

    Optional<ProfessorEntity> findByAlias(String alias);
}
