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
@Table(name = "documents")
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("DOCUMENT")
public class DocumentEntity extends ContentEntity {
    @NotNull
    @Column(name = "num_pages", nullable = false)
    private Integer numPages;

    @NotNull
    @Lob
    @Column(name = "link", columnDefinition = "text", nullable = false)
    private String link;

    public DocumentEntity(String title, String description, Integer numPages, String link) {
        setTitle(title);
        setDescription(description);
        setNumPages(numPages);
        setLink(link);
    }

}
