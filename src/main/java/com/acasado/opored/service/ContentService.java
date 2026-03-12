package com.acasado.opored.service;

import com.acasado.opored.dto.ContentDTO;
import com.acasado.opored.dto.DocumentDTO;
import com.acasado.opored.dto.QuizDTO;
import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.model.ContentEntity;
import com.acasado.opored.model.DocumentEntity;
import com.acasado.opored.model.QuizEntity;
import com.acasado.opored.model.VideoEntity;
import com.acasado.opored.repository.DocumentRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final QuizRepository quizRepository;
    private final DocumentRepository documentRepository;
    private final VideoRepository videoRepository;
    private final QuizService quizService;


    ContentEntity convertToEntity(ContentDTO contentDTO) {
        if (contentDTO instanceof QuizDTO) {
            QuizEntity quizEntity = new QuizEntity(
                    contentDTO.getTitle(),
                    contentDTO.getDescription(),
                    ((QuizDTO) contentDTO).getAllowedAttempts(),
                    ((QuizDTO) contentDTO).getTimeLimit(),
                    ((QuizDTO) contentDTO).getScoreToPass(),
                    ((QuizDTO) contentDTO).getMaxScore(),
                    quizService.setQuestionEntities(((QuizDTO) contentDTO).getQuestions()));
            quizRepository.save(quizEntity);
            return quizEntity;
        } else if (contentDTO instanceof DocumentDTO) {
            DocumentEntity documentEntity = new DocumentEntity(
                    contentDTO.getTitle(),
                    contentDTO.getDescription(),
                    ((DocumentDTO) contentDTO).getNumPages(),
                    ((DocumentDTO) contentDTO).getLink());
            documentRepository.save(documentEntity);
            return documentEntity;
        }
        else if (contentDTO instanceof VideoDTO){
            VideoEntity videoEntity = new VideoEntity(
                    contentDTO.getTitle(),
                    contentDTO.getDescription(),
                    ((VideoDTO) contentDTO).getDuration(),
                    ((VideoDTO) contentDTO).getLink());
            videoRepository.save(videoEntity);
            return videoEntity;
        }
        return null;
    }
}
