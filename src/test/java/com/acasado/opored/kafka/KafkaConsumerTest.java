package com.acasado.opored.kafka;

import com.acasado.opored.dto.kafka.BocylAnnouncementDTO;
import com.acasado.opored.dto.kafka.BoeAnnouncementDTO;
import com.acasado.opored.dto.kafka.BorAnnouncementDTO;
import com.acasado.opored.service.AnnouncementAssignmentService;
import com.acasado.opored.service.kafka.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private AnnouncementAssignmentService announcementAssignmentService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    @DisplayName("Should process BOE announcement correctly")
    void whenListenBoe_thenServiceCategorizationIsCalled() {
        // Arrange
        BoeAnnouncementDTO dto = new BoeAnnouncementDTO(
                "BOE Title",
                "https://html",
                "https://pdf",
                "ID-123",
                LocalDate.now()
        );

        // Act
        kafkaConsumer.listenBoe(dto);

        // Assert
        verify(announcementAssignmentService).kafkaAnnouncementCategorization(dto);
    }

    @Test
    @DisplayName("Should process BOCYL announcement correctly")
    void whenListenBocyl_thenServiceCategorizationIsCalled() {
        // Arrange
        BocylAnnouncementDTO dto = new BocylAnnouncementDTO(
                "BOCYL Title",
                "https://html",
                "https://pdf",
                LocalDate.now()
        );

        // Act
        kafkaConsumer.listenBocyl(dto);

        // Assert
        verify(announcementAssignmentService).kafkaAnnouncementCategorization(dto);
    }

    @Test
    @DisplayName("Should process BOR announcement correctly")
    void whenListenBor_thenServiceCategorizationIsCalled() {
        // Arrange
        BorAnnouncementDTO dto = new BorAnnouncementDTO(
                "BOR Title",
                "https://html",
                "https://pdf",
                LocalDate.now()
        );

        // Act
        kafkaConsumer.listenBor(dto);

        // Assert
        verify(announcementAssignmentService).kafkaAnnouncementCategorization(dto);
    }
}