package com.acasado.opored.dto.kafka;

import com.acasado.opored.model.PublicExaminationEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
@Builder
public class ClassificationResult {

    private PublicExaminationEntity publicExamination;
    private Double score;
    private Double confidence;
    private Map<PublicExaminationEntity, Double> allScores;
    private boolean requiresManualReview;

    public static ClassificationResult unclassified() {
        return ClassificationResult.builder()
                .publicExamination(null)
                .score(0.0)
                .confidence(0.0)
                .allScores(Collections.emptyMap())
                .requiresManualReview(true)
                .build();
    }

    public boolean isClassified() {
        return publicExamination != null;
    }
}
