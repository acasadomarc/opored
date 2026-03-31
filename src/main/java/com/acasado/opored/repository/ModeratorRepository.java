package com.acasado.opored.repository;

import com.acasado.opored.model.ModeratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<ModeratorEntity, Integer> {
    Optional<ModeratorEntity> findByEmail(String email);

    Optional<ModeratorEntity> findFirstByIdNot(Integer id);

}
