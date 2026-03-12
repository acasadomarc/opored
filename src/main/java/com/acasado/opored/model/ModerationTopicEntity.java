package com.acasado.opored.model;


import jakarta.persistence.*;
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
@Table(name="moderation_topic")
@SQLRestriction("is_deleted = false")
public class ModerationTopicEntity {

    @EmbeddedId
    private ModerationTopicId id;

    @MapsId("topicId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicEntity topic;

    @MapsId("moderatorId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "moderator_id", nullable = false)
    private ModeratorEntity moderator;

    @CreationTimestamp
    @Column(name = "moderation_date", updatable = false)
    private LocalDate moderationDate;

    @Lob
    @Column(name = "reason", nullable = false, columnDefinition = "text")
    private String reason;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    public ModerationTopicEntity(ModerationTopicId id, TopicEntity topic, ModeratorEntity moderator, String reason) {
        setId(id);
        setTopic(topic);
        setModerator(moderator);
        setReason(reason);
    }
}
