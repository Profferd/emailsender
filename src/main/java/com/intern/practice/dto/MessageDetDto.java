package com.intern.practice.dto;

import com.intern.practice.entity.MessageStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageDetDto {

    private String id;
    private String subject;
    private String content;
    private List<String> destination;
    private MessageStatus status;
    private String error;

}
