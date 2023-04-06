package com.intern.practice.messaging;

import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class KafkaMessage {
    private String transactionId;

    @NotEmpty(message = "This field can't be blank")
    private String subject;
    @NotEmpty(message = "This field can't be blank")
    private String content;
    @Email
    @NotEmpty(message = "This field can't be blank")
    private List<String> email;
}
