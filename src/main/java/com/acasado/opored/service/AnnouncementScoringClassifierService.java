package com.acasado.opored.service;

import com.acasado.opored.dto.kafka.ClassificationResult;
import com.acasado.opored.dto.kafka.KafkaAnnouncementDTO;
import com.acasado.opored.model.AnnouncementClassificationKeywords;
import com.acasado.opored.model.PublicExaminationEntity;
import com.acasado.opored.repository.AnnouncementClassificationKeywordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnnouncementScoringClassifierService {

    private static final double MAIN_TAG_WEIGHT = 10.0;
    private static final double SECONDARY_TAG_WEIGHT = 3.0;
    private static final double EXCLUSION_TAG_PENALTY = -15.0;
    private static final double MIN_CONFIDENCE_THRESHOLD = 5.0;

    private final AnnouncementClassificationKeywordsRepository announcementClassificationKeywordsRepository;

    public ClassificationResult classify(KafkaAnnouncementDTO announcement) {
        String normalizedText = normalizeText(announcement.getTitle());

        Map<PublicExaminationEntity, Double> scores = calculateScores(normalizedText);

        if (scores.isEmpty()) {
            return ClassificationResult.unclassified();
        }

        // Find the best coincidence
        Map.Entry<PublicExaminationEntity, Double> bestMatch = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        double confidence = calculateConfidence(bestMatch.getValue(), scores);

        return ClassificationResult.builder()
                .publicExamination(bestMatch.getKey())
                .score(bestMatch.getValue())
                .confidence(confidence)
                .requiresManualReview(confidence < MIN_CONFIDENCE_THRESHOLD)
                .build();
    }

    private Map<PublicExaminationEntity, Double> calculateScores(String normalizedText) {
        List<AnnouncementClassificationKeywords> allKeywords = getKeywords();
        Map<PublicExaminationEntity, Double> scores = new HashMap<>();

        for (AnnouncementClassificationKeywords keywords : allKeywords) {
            double score = calculateScoreForExamination(normalizedText, keywords);

            // Only include it if the score is positive
            if (score > 0) {
                scores.put(keywords.getPublicExamination(), score);
            }
        }

        return scores;
    }

    private double calculateScoreForExamination(String text, AnnouncementClassificationKeywords keywords) {
        double score = 0.0;

        // Main tags
        for (String tag : keywords.getMainTags().split(",")) {
            if (text.contains(tag)) {
                score += MAIN_TAG_WEIGHT;
            }
        }

        // Secondary tags
        for (String tag : keywords.getSecondaryTags().split(",")) {
            if (text.contains(tag)) {
                score += SECONDARY_TAG_WEIGHT;
            }
        }

        // Exclusion tags (penalty)
        for (String tag : keywords.getExclusionTags().split(",")) {
            if (text.contains(tag)) {
                score += EXCLUSION_TAG_PENALTY;
            }
        }

        return score;
    }

    private double calculateConfidence(double bestScore, Map<PublicExaminationEntity, Double> allScores) {
        if (allScores.size() == 1) {
            return bestScore > MIN_CONFIDENCE_THRESHOLD ? 10.0 : bestScore;
        }

        List<Double> sortedScores = allScores.values().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        double best = sortedScores.get(0);
        double secondBest = sortedScores.size() > 1 ? sortedScores.get(1) : 0.0;

        // Confidence is the difference between the best and the second best option
        return best - secondBest;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Remove diacritical marks
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ") // Only letters, numbers, and spaces
                .replaceAll("\\s+", " ") // Normalizing spaces
                .trim();
    }

    private List<AnnouncementClassificationKeywords> getKeywords() {
        return announcementClassificationKeywordsRepository.findAll();
    }
}
