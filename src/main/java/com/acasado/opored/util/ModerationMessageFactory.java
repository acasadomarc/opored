package com.acasado.opored.util;

import com.acasado.opored.dto.ModerationMessageDTO;
import com.acasado.opored.enumeration.StatusEnum;
import com.acasado.opored.model.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ModerationMessageFactory {

    public static ModerationMessageDTO createValidModerationMessageDTO() {
        return new ModerationMessageDTO(
                100, // Message ID
                5,   // Moderator ID
                LocalDate.now(),
                "Inappropriate content"
        );
    }

    public static ModerationMessageEntity createValidModerationMessageEntity() {
        ModerationMessageId id = new ModerationMessageId(100, 5);

        MessageEntity message = new MessageEntity();
        message.setId(100);
        message.setStatus(StatusEnum.VISIBLE);

        ModeratorEntity moderator = new ModeratorEntity();
        moderator.setId(5);

        ModerationMessageEntity entity = new ModerationMessageEntity();
        entity.setId(id);
        entity.setMessage(message);
        entity.setModerator(moderator);
        entity.setModerationDate(LocalDate.now());
        entity.setReason("Inappropriate content");
        entity.setIsDeleted(false);

        return entity;
    }
}