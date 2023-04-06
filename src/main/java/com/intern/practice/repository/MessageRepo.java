package com.intern.practice.repository;

import com.intern.practice.entity.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepo {

    Optional<Message> findById(String id);

    List<Message> read();

    String save(Message message);

}
