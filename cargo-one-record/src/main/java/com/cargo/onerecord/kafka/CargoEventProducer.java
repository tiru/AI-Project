package com.cargo.onerecord.kafka;

import com.cargo.onerecord.dto.kafka.CargoKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargoEventProducer {

    private final KafkaTemplate<String, CargoKafkaEvent> kafkaTemplate;

    /**
     * Publish a domain event to the given Kafka topic.
     * The objectId is used as the partition key to ensure ordering per logistics object.
     */
    public void publish(String topic, CargoKafkaEvent event) {
        // Stamp event metadata if not already set
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }
        if (event.getOccurredAt() == null) {
            event.setOccurredAt(OffsetDateTime.now());
        }

        kafkaTemplate.send(topic, event.getObjectId(), event)
                .thenAccept(result -> log.info(
                        "[KAFKA] Published  topic={} eventType={} objectId={} partition={} offset={}",
                        topic,
                        event.getEventType(),
                        event.getObjectId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("[KAFKA] Failed to publish  topic={} eventType={} objectId={} error={}",
                            topic, event.getEventType(), event.getObjectId(), ex.getMessage());
                    return null;
                });
    }

    // --- Convenience builders ---

    public CargoKafkaEvent buildEvent(String eventType, String objectId,
                                      String objectType, String objectRef,
                                      String status, String performedBy,
                                      Object payload) {
        return CargoKafkaEvent.builder()
                .eventType(eventType)
                .objectId(objectId)
                .objectType(objectType)
                .objectRef(objectRef)
                .status(status)
                .performedBy(performedBy)
                .payload(payload)
                .build();
    }
}