package com.cargo.onerecord.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_SHIPMENTS        = "cargo.shipments";
    public static final String TOPIC_LOGISTICS_EVENTS = "cargo.logistics-events";
    public static final String TOPIC_BOOKINGS         = "cargo.bookings";
    public static final String TOPIC_CHANGE_REQUESTS  = "cargo.change-requests";

    @Bean
    public NewTopic shipmentsTopic() {
        return TopicBuilder.name(TOPIC_SHIPMENTS).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic logisticsEventsTopic() {
        return TopicBuilder.name(TOPIC_LOGISTICS_EVENTS).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic bookingsTopic() {
        return TopicBuilder.name(TOPIC_BOOKINGS).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic changeRequestsTopic() {
        return TopicBuilder.name(TOPIC_CHANGE_REQUESTS).partitions(3).replicas(1).build();
    }
}