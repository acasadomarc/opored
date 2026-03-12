package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "videos")
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("VIDEO")
public class VideoEntity extends ContentEntity {
    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Lob
    @Column(name = "link",columnDefinition = "text", nullable = false)
    private String link;

    public VideoEntity(String title, String description, Integer duration, String link) {
        setTitle(title);
        setDescription(description);
        setDuration(duration);
        setLink(link);
    }
}
