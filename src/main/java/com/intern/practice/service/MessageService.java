package com.intern.practice.service;

import com.intern.practice.dto.MessageDetDto;
import com.intern.practice.dto.MessageDto;
import com.intern.practice.messaging.KafkaMessage;

import java.util.List;

public interface MessageService {

    String save(KafkaMessage kafkaMessage);

    List<MessageDetDto> read();

    void update(MessageDetDto messageDetDto);

}
