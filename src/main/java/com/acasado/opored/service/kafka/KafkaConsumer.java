package com.acasado.opored.service.kafka;

import com.acasado.opored.dto.kafka.BocylAnnouncementDTO;
import com.acasado.opored.dto.kafka.BoeAnnouncementDTO;
import com.acasado.opored.dto.kafka.BorAnnouncementDTO;
import com.acasado.opored.service.AnnouncementAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final AnnouncementAssignmentService announcementAssignmentService;

    @KafkaListener(topics = "announcementsBOE-topic", containerFactory = "kafkaListenerContainerBoeFactory")
    public void listenBoe(BoeAnnouncementDTO announcementDTO) {
        log.info("Received BOE announcement");
        announcementAssignmentService.kafkaAnnouncementCategorization(announcementDTO);
    }

    @KafkaListener(topics = "announcementsBOCYL-topic", containerFactory = "kafkaListenerContainerBocylFactory")
    public void listenBocyl(BocylAnnouncementDTO announcementDTO) {
        log.info("Received BOCYL announcement");
        announcementAssignmentService.kafkaAnnouncementCategorization(announcementDTO);
    }

    @KafkaListener(topics = "announcementsBOR-topic", containerFactory = "kafkaListenerContainerBorFactory")
    public void listenBor(BorAnnouncementDTO announcementDTO) {
        log.info("Received BOR announcement");
        announcementAssignmentService.kafkaAnnouncementCategorization(announcementDTO);
    }

}