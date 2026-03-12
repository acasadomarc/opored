package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant expiryDate;

    private boolean revoked = false;

    @ManyToOne(optional = false)
    private UserEntity user;
}
