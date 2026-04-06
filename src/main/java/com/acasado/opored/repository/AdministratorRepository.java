package com.acasado.opored.repository;

import com.acasado.opored.model.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Integer> {
    Optional<AdministratorEntity> findByEmail(String email);
}
