package com.intern.practice.controller;

import com.intern.practice.dto.MessageDto;
import com.intern.practice.entity.Message;
import com.intern.practice.messaging.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    @Value("${kafka.topic.paymentReceived}")
    private String message;

    private final KafkaOperations<String, KafkaMessage> kafkaOperations;

    public void receiveMessageToSend(@RequestBody MessageDto messageDto) {
        kafkaOperations.send(message, messageDto.getSubject(), toMessage(messageDto));
    }

    private KafkaMessage toMessage(MessageDto messageDto) {
        return KafkaMessage.builder()
                .subject(messageDto.getSubject())
                .content(messageDto.getContent())
                .email(messageDto.getEmail())
                .build();
    }
}
