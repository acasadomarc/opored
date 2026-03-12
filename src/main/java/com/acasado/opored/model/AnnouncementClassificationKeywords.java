package com.acasado.opored.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announcements_classification_keywords")
public class AnnouncementClassificationKeywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "main_tags", columnDefinition = "TEXT")
    private String mainTags;

    @Column(name = "secondary_tags", columnDefinition = "TEXT")
    private String secondaryTags;

    @Column(name = "exclusion_tags", columnDefinition = "TEXT")
    private String exclusionTags;

    @OneToOne(fetch = FetchType.EAGER) // Avoid LazyInitializationException on announcement classification
    @JoinColumn(name = "public_examination_id", unique = true)
    private PublicExaminationEntity publicExamination;

}