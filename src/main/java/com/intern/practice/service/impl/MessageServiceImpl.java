package com.intern.practice.service.impl;

import com.intern.practice.dto.MessageDetDto;
import com.intern.practice.dto.MessageDto;
import com.intern.practice.entity.Message;
import com.intern.practice.entity.MessageStatus;
import com.intern.practice.messaging.KafkaMessage;
import com.intern.practice.repository.MessageRepo;
import com.intern.practice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepo messageRepo;

    @Override
    public String save(KafkaMessage kafkaMessage) {
        Message message = new Message();
        message.setSubject(kafkaMessage.getSubject());
        message.setSubject(kafkaMessage.getContent());
        message.setDestination(kafkaMessage.getEmail());
        message.setMessageStatus(MessageStatus.PENDING);
        return messageRepo.save(message);
    }

    @Override
    public List<MessageDetDto> read() {
        return messageRepo.read().stream()
                .map(this::messageDataToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void update(MessageDetDto messageDetDto) {
        Message message = messageRepo.findById(messageDetDto.getId()).orElseThrow();
        message.setMessageStatus(messageDetDto.getStatus());
        message.setErrorMessage(messageDetDto.getError());
    }

    private MessageDetDto messageDataToDto(Message message) {
        return MessageDetDto.builder()
                .id(message.getId())
                .subject(message.getSubject())
                .content(message.getContent())
                .destination(message.getDestination())
                .status(message.getMessageStatus())
                .error(message.getErrorMessage())
                .build();
    }
}
