package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@SQLRestriction("is_deleted = false")
public abstract class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "alias", nullable = false)
    private String alias;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @CreationTimestamp
    @Column(name="registration_date", updatable = false)
    private LocalDate registrationDate;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "account_no_expired", nullable = false)
    private boolean accountNoExpired;

    @Column(name = "account_no_locked", nullable = false)
    private boolean accountNoLocked;

    @Column(name = "credential_no_expired", nullable = false)
    private boolean credentialNoExpired;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role", nullable = false)
    private RoleEntity role;

    protected UserEntity(String name, String surname, String alias, String email, String password, UserAccountStatus accountStatus, RoleEntity role) {
        setName(name);
        setSurname(surname);
        setAlias(alias);
        setEmail(email);
        setPassword(password);
        setEnabled(accountStatus.isEnabled());
        setAccountNoExpired(accountStatus.isAccountNoExpired());
        setAccountNoLocked(accountStatus.isAccountNoLocked());
        setCredentialNoExpired(accountStatus.isCredentialNoExpired());
        setRole(role);
    }
}
