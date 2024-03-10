package com.example.EventGenerator.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class EventPublisherService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishEvent(String topic, String key, String event) {
        kafkaTemplate.send(topic, key, event);
    }
}
