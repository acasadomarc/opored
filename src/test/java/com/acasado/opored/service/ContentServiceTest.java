package com.acasado.opored.service;

import com.acasado.opored.dto.*;
import com.acasado.opored.model.*;
import com.acasado.opored.repository.DocumentRepository;
import com.acasado.opored.repository.QuizRepository;
import com.acasado.opored.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private QuizService quizService;

    @InjectMocks
    private ContentService contentService;

    @Test
    void When_ConvertToEntity_WithQuizDTO_Expect_QuizEntitySaved() {
        // Arrange
        QuizDTO quizDTO = createMockQuizDTO();
        Set<QuestionEntity> mockQuestions = new HashSet<>();
        when(quizService.setQuestionEntities(quizDTO.getQuestions())).thenReturn(mockQuestions);

        // Act
        ContentEntity result = contentService.convertToEntity(quizDTO);

        // Assert
        assertNotNull(result);
        assertInstanceOf(QuizEntity.class, result);

        QuizEntity savedEntity = (QuizEntity) result;
        assertEquals("Sample Quiz", savedEntity.getTitle());
        assertEquals(60, savedEntity.getTimeLimit());
        assertEquals(5, savedEntity.getScoreToPass());
        assertEquals(10, savedEntity.getMaxScore());

        verify(quizRepository).save(any(QuizEntity.class));
        verify(quizService).setQuestionEntities(quizDTO.getQuestions());
        verifyNoInteractions(documentRepository, videoRepository);
    }

    @Test
    void When_ConvertToEntity_WithDocumentDTO_Expect_DocumentEntitySaved() {
        // Arrange
        DocumentDTO documentDTO = createMockDocumentDTO();

        // Act
        ContentEntity result = contentService.convertToEntity(documentDTO);

        // Assert
        assertNotNull(result);
        assertInstanceOf(DocumentEntity.class, result);

        ArgumentCaptor<DocumentEntity> captor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(documentRepository).save(captor.capture());

        DocumentEntity savedEntity = captor.getValue();
        assertEquals("Sample Document", savedEntity.getTitle());
        assertEquals(25, savedEntity.getNumPages());
        assertEquals("https://docs.example.com/file.pdf", savedEntity.getLink());

        verifyNoInteractions(quizRepository, videoRepository, quizService);
    }

    @Test
    void When_ConvertToEntity_WithVideoDTO_Expect_VideoEntitySaved() {
        // Arrange
        VideoDTO videoDTO = createMockVideoDTO();

        // Act
        ContentEntity result = contentService.convertToEntity(videoDTO);

        // Assert
        assertNotNull(result);
        assertInstanceOf(VideoEntity.class, result);

        ArgumentCaptor<VideoEntity> captor = ArgumentCaptor.forClass(VideoEntity.class);
        verify(videoRepository).save(captor.capture());

        VideoEntity savedEntity = captor.getValue();
        assertEquals("Sample Video", savedEntity.getTitle());
        assertEquals("https://video.example.com/watch", savedEntity.getLink());

        verifyNoInteractions(quizRepository, documentRepository, quizService);
    }

    @Test
    void When_ConvertToEntity_WithUnknownDTO_Expect_Null() {
        // Arrange
        ContentDTO unknownDTO = new ContentDTO() {
            @Override
            public String getTitle() {
                return "Unknown";
            }
        };

        // Act
        ContentEntity result = contentService.convertToEntity(unknownDTO);

        // Assert
        assertNull(result);
        verifyNoInteractions(quizRepository, documentRepository, videoRepository, quizService);
    }

    // --- Helper Methods ---

    private QuizDTO createMockQuizDTO() {
        QuizDTO dto = new QuizDTO();
        dto.setId(1);
        dto.setTitle("Sample Quiz");
        dto.setDescription("Quiz Description");
        dto.setTimeLimit(60);
        dto.setScoreToPass(5);
        dto.setMaxScore(10);
        dto.setQuestions(new HashSet<>());
        return dto;
    }

    private DocumentDTO createMockDocumentDTO() {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(2);
        dto.setTitle("Sample Document");
        dto.setDescription("Document Description");
        dto.setNumPages(25);
        dto.setLink("https://docs.example.com/file.pdf");
        return dto;
    }

    private VideoDTO createMockVideoDTO() {
        VideoDTO dto = new VideoDTO();
        dto.setId(3);
        dto.setTitle("Sample Video");
        dto.setDescription("Video Description");
        dto.setLink("https://video.example.com/watch");
        return dto;
    }
}