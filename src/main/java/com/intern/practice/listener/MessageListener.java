package com.intern.practice.listener;

import com.intern.practice.dto.MessageDetDto;
import com.intern.practice.entity.MessageStatus;
import com.intern.practice.messaging.KafkaMessage;
import com.intern.practice.service.MessageService;
import com.intern.practice.service.impl.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final MessageService messageService;

    private final MailService mailService;

    @KafkaListener(topics = "${kafka.topic.paymentReceived}")
    public void messageRec(KafkaMessage kafkaMessage) {
        String id = messageService.save(kafkaMessage);
        mailService.sendMessage(getMessageDetDto(kafkaMessage, id));
    }

    private MessageDetDto getMessageDetDto(KafkaMessage kafkaMessage, String id) {
        return MessageDetDto.builder()
                .id(id)
                .subject(kafkaMessage.getSubject())
                .content(kafkaMessage.getContent())
                .destination(kafkaMessage.getEmail())
                .status(MessageStatus.PENDING)
                .build();
    }

}
