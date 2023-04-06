package com.intern.practice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class MessageDto {
    @NotEmpty(message = "This field can't be blank")
    private String subject;
    @NotEmpty(message = "This field can't be blank")
    private String content;
    @Email
    @NotEmpty(message = "This field can't be blank")
    private List<String> email;
}
