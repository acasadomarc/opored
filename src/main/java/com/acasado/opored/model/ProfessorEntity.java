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
@Table(name = "professors")
public class ProfessorEntity extends UserEntity {
    @ManyToMany
    @JoinTable(name = "rating_professors",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<RatingProfessorEntity> ratings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "professor")
    private Set<CourseEntity> courses = new LinkedHashSet<>();

    public ProfessorEntity(String name, String surname, String alias, String email, String password, UserAccountStatus accountStatus, RoleEntity role, Set<RatingProfessorEntity> ratings) {
        super(name, surname, alias, email, password, accountStatus, role);
        setRatings(ratings);
    }
}
