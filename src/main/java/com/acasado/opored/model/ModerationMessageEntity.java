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
@Table(name="moderation_message")
@SQLRestriction("is_deleted = false")
public class ModerationMessageEntity {

    @EmbeddedId
    private ModerationMessageId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private MessageEntity message;

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

    public ModerationMessageEntity(ModerationMessageId id, MessageEntity message, ModeratorEntity moderator, String reason) {
        setId(id);
        setMessage(message);
        setModerator(moderator);
        setReason(reason);
    }
}
