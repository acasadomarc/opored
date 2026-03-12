package com.acasado.opored.repository;

import com.acasado.opored.enumeration.RoleEnum;
import com.acasado.opored.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity getRoleByName(RoleEnum name);
}
