package com.acasado.opored.repository;

import com.acasado.opored.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
    Optional<StudentEntity> findByEmail(String email);

    /* The next  query is necessary for the promoteUserService. Trying to insert or delete the items
     with save() or delete() will also try to delete them from users table, and we only need them added to
     students table */

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO students (id) VALUES (?1)", nativeQuery = true)
    void insertStudent(Integer id);

    Optional<StudentEntity> findByAlias(String alias);

}
