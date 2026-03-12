package com.acasado.opored.dto;

import com.acasado.opored.model.DocumentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Document content details")
public class DocumentDTO extends ContentDTO{
    @Schema(description = "Number of pages in the document", example = "25")
    private Integer numPages;

    @Schema(example = "https://storage.example.com/docs/law-summary.pdf")
    private String link;

    public DocumentDTO(DocumentEntity documentEntity) {
        setId(documentEntity.getId());
        setTitle(documentEntity.getTitle());
        setDescription(documentEntity.getDescription());
        setNumPages(documentEntity.getNumPages());
        setLink(documentEntity.getLink());
        if (documentEntity.getCourse() != null) {
            setCourseId(documentEntity.getCourse().getId());
        }
    }

    public DocumentDTO(Integer id, String title, String description, Integer numPages,String link, Integer courseId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setNumPages(numPages);
        setLink(link);
        setCourseId(courseId);
    }
}