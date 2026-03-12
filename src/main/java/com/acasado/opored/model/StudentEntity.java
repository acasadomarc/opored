package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "students")
public class StudentEntity extends UserEntity {
    @ManyToMany(mappedBy = "students")
    private Set<PublicExaminationEntity> publicExaminations = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "follow_topic",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<TopicEntity> topicsFollowed = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<PurchaseEntity> purchases = new LinkedHashSet<>();

    public StudentEntity(String name, String surname, String alias, String email, String password, UserAccountStatus accountStatus, RoleEntity role) {
        super(name, surname, alias, email, password, accountStatus, role);
    }
}
