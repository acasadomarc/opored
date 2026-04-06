package com.acasado.opored.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "moderators")
public class ModeratorEntity extends UserEntity {
    @OneToMany(mappedBy = "moderator")
    private Set<ModerationMessageEntity> moderationMessages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "moderator")
    private Set<ModerationTopicEntity> moderationTopics = new LinkedHashSet<>();

    public ModeratorEntity(UserIdentificationFields userIdentificationFields, UserAccountStatus accountStatus,  RoleEntity role) {
        super(userIdentificationFields, accountStatus, role);
    }
}
