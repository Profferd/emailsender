package com.intern.practice.service.impl;

import com.intern.practice.dto.MessageDetDto;
import com.intern.practice.entity.MessageStatus;
import com.intern.practice.service.MessageService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class MailService {

    private final JavaMailSender mailSender;

    private final MessageService messageService;

    @Value("${sender-email}")
    private String senderMail;

    @Scheduled(fixedDelay = 400000L, initialDelay = 150000L)
    public void mailIsNotSucceed() {
        List<MessageDetDto> notSucceed = messageService.read();
        notSucceed.forEach(this::sendMessage);
    }

    public void sendMessage(MessageDetDto messageDetDto) {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mail, "utf-8");

        try {
            setMessage(mimeMessageHelper, messageDetDto);
            mailSender.send(mail);
            updateMessageStatus(messageDetDto, MessageStatus.SUCCESS, null);
        } catch (MessagingException e) {
            System.out.println("yep");
            updateMessageStatus(messageDetDto, MessageStatus.ERROR,
                    String.format("%s: %s", e.getClass(), e.getMessage()));
        }
    }

    private void setMessage(MimeMessageHelper message, MessageDetDto messageDetDto) throws MessagingException {
        String[] destination = messageDetDto.getDestination().toArray(new String[0]);
        message.setFrom(senderMail);
        message.setSubject(messageDetDto.getSubject());
        message.setText(messageDetDto.getContent());
        message.setTo(destination);
    }

    private void updateMessageStatus(MessageDetDto messageDetDto, MessageStatus status, String error) {
        messageDetDto.setStatus(status);
        messageDetDto.setError(error);
    }

}
