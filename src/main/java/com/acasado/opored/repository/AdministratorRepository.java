package com.acasado.opored.repository;

import com.acasado.opored.model.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Integer> {
    Optional<AdministratorEntity> findByEmail(String email);

    /* The next two queries are necessary for the promoteUserService. Trying to insert or delete the items
     with save() or delete() will also try to delete them from users table, and we only need them removed/added from/to
     administrators table */

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO administrators (id) VALUES (?1)", nativeQuery = true)
    void insertAdministrator(Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM administrators WHERE id = (?1)", nativeQuery = true)
    void deleteAdministrator(Integer id);

    Optional<AdministratorEntity> findByAlias(String alias);
}
