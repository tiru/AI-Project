package com.cargo.onerecord.kafka;

import com.cargo.onerecord.dto.kafka.CargoKafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CargoEventConsumer {

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_SHIPMENTS,
                   groupId = "cargo-one-record-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeShipmentEvent(
            @Payload CargoKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("[KAFKA][{}] SHIPMENT  eventType={} objectId={} status={} by={} partition={} offset={}",
                KafkaTopicConfig.TOPIC_SHIPMENTS,
                event.getEventType(), event.getObjectId(),
                event.getStatus(), event.getPerformedBy(),
                partition, offset);
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_LOGISTICS_EVENTS,
                   groupId = "cargo-one-record-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeLogisticsEvent(
            @Payload CargoKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("[KAFKA][{}] LOGISTICS_EVENT  eventType={} objectId={} eventCode={} by={} partition={} offset={}",
                KafkaTopicConfig.TOPIC_LOGISTICS_EVENTS,
                event.getEventType(), event.getObjectId(),
                event.getStatus(), event.getPerformedBy(),
                partition, offset);
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_BOOKINGS,
                   groupId = "cargo-one-record-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeBookingEvent(
            @Payload CargoKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("[KAFKA][{}] BOOKING  eventType={} objectId={} status={} by={} partition={} offset={}",
                KafkaTopicConfig.TOPIC_BOOKINGS,
                event.getEventType(), event.getObjectId(),
                event.getStatus(), event.getPerformedBy(),
                partition, offset);
    }

    @KafkaListener(topics = KafkaTopicConfig.TOPIC_CHANGE_REQUESTS,
                   groupId = "cargo-one-record-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeChangeRequestEvent(
            @Payload CargoKafkaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("[KAFKA][{}] CHANGE_REQUEST  eventType={} objectId={} status={} by={} partition={} offset={}",
                KafkaTopicConfig.TOPIC_CHANGE_REQUESTS,
                event.getEventType(), event.getObjectId(),
                event.getStatus(), event.getPerformedBy(),
                partition, offset);
    }
}