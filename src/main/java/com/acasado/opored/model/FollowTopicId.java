package com.acasado.opored.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FollowTopicId {
    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;
}