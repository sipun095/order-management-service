package com.sp.ordermanagement.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {


//    @KafkaListener(topics = "order-events", groupId = "order-group")
//    public void listen(String message) {
//        System.out.println("Received Kafka message: " + message);
//        // Further processing of the message
//    }
}
