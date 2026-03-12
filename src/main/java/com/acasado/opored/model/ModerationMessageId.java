package com.acasado.opored.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ModerationMessageId {
    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "moderator_id", nullable = false)
    private Integer moderatorId;
}
