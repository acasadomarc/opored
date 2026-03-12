package com.acasado.opored.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AnnouncementStagingId {
    private String title;
    private LocalDate publicationDate;
}
