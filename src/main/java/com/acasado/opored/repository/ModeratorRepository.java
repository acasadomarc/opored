package com.acasado.opored.repository;

import com.acasado.opored.model.ModeratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeratorRepository extends JpaRepository<ModeratorEntity, Integer> {
    Optional<ModeratorEntity> findByEmail(String email);

    /* The next two queries are necessary for the promoteUserService. Trying to insert or delete the items
     with save() or delete() will also try to delete them from users table, and we only need them removed/added from/to
     moderators table */

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO moderators (id) VALUES (?1)", nativeQuery = true)
    void insertModerator(Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM moderators WHERE id = (?1)", nativeQuery = true)
    void deleteModerator(Integer id);

    Optional<ModeratorEntity> findByAlias(String alias);

}
