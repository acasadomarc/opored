package com.acasado.opored.service.kafka;

import com.acasado.opored.dto.kafka.BocylAnnouncementDTO;
import com.acasado.opored.dto.kafka.BoeAnnouncementDTO;
import com.acasado.opored.dto.kafka.BorAnnouncementDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    private static final String GROUP_ID = "sample-consumer-group";
    private static final String AUTO_OFFSET_RESET = "earliest";

    @Bean
    public ConsumerFactory<String, BoeAnnouncementDTO> consumerBoeFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);

        JsonDeserializer<BoeAnnouncementDTO> valueDeserializer =
                new JsonDeserializer<>(BoeAnnouncementDTO.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.ignoreTypeHeaders(); // Prevent it from trying to look for the BoeAnnouncementDTO class in the scraping service

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BoeAnnouncementDTO> kafkaListenerContainerBoeFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BoeAnnouncementDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBoeFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BocylAnnouncementDTO> consumerBocylFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);

        JsonDeserializer<BocylAnnouncementDTO> valueDeserializer =
                new JsonDeserializer<>(BocylAnnouncementDTO.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.ignoreTypeHeaders(); // Prevent it from trying to look for the BocylAnnouncementDTO class in the scraping service

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BocylAnnouncementDTO> kafkaListenerContainerBocylFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BocylAnnouncementDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBocylFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BorAnnouncementDTO> consumerBorFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);

        JsonDeserializer<BorAnnouncementDTO> valueDeserializer =
                new JsonDeserializer<>(BorAnnouncementDTO.class);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.ignoreTypeHeaders(); // Prevent it from trying to look for the BorAnnouncementDTO class in the scraping service

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BorAnnouncementDTO> kafkaListenerContainerBorFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BorAnnouncementDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBorFactory());
        return factory;
    }
}