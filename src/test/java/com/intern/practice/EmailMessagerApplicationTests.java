package com.intern.practice;

import com.intern.practice.entity.Message;
import com.intern.practice.entity.MessageStatus;
import com.intern.practice.messaging.KafkaMessage;
import com.intern.practice.service.MessageService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmailMessagerApplication.class, ElasticsearchTest.class})
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class EmailMessagerApplicationTests {

    @Value("${kafka.topic.service}")
    private String kafkaTopic;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private KafkaOperations<String, KafkaMessage> kafkaOperations;

    @SpyBean
    private MessageService messageService;

    @MockBean
    private JavaMailSender mailSender;

    private final String correctEmail = "test@mail.com";
    private final String incorrectEmail = "test";

    @BeforeEach
    void setUp() {
        if(!elasticsearchOperations.indexOps(Message.class).exists()) {
            elasticsearchOperations.indexOps(Message.class).createMapping();
        }
    }

    @Test
    void whenSendMessageToKafkaWithCorrectMail_thenReceivedMessageSaveToDBAndSendToRecipients() {
        doNothing().when(mailSender).send((MimeMessage) any());
        doReturn(new MimeMessage((Session) null)).when(mailSender).createMimeMessage();

        KafkaMessage receivedMessage = buildMessage(correctEmail);
        kafkaOperations.send(kafkaTopic, receivedMessage.getSubject(), receivedMessage);

        verify(messageService, after(5000)).save(any());
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send((MimeMessage) any());

        List<Message> succeededMessages = getAllMessagesByStatus(MessageStatus.SUCCESS);
        assertThat(succeededMessages.size()).isEqualTo(1);
        assertThat(succeededMessages.get(0).getSubject()).isEqualTo("Subject");
        assertThat(succeededMessages.get(0).getContent()).isEqualTo("Content");
        assertThat(succeededMessages.get(0).getDestination()).isEqualTo(List.of(correctEmail));
    }

    @Test
    void whenSendMessageToKafkaWithIncorrectMail_thenReceivedMessageSaveToDBAndThrowAddressException() {
        doThrow(MailSendException.class).when(mailSender).send((MimeMessage) any());
        doReturn(new MimeMessage((Session) null)).when(mailSender).createMimeMessage();

        KafkaMessage receivedMessage = buildMessage(incorrectEmail);
        kafkaOperations.send(kafkaTopic, receivedMessage.getSubject(), receivedMessage);

        verify(messageService, after(5000)).save(any());
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send((MimeMessage) any());

        List<Message> errorMessages = getAllMessagesByStatus(MessageStatus.ERROR);
        assertThat(errorMessages.size()).isEqualTo(1);
        assertThat(errorMessages.get(0).getSubject()).isEqualTo("Subject");
        assertThat(errorMessages.get(0).getContent()).isEqualTo("Content");
        assertThat(errorMessages.get(0).getDestination()).isEqualTo(List.of(incorrectEmail));
        assertThat(errorMessages.get(0).getErrorMessage()).contains("org.springframework.mail.MailSendException");
    }


    private KafkaMessage buildMessage(String emails) {
        return KafkaMessage.builder()
                .subject("Test subject")
                .content("Test content")
                .email(List.of(emails))
                .build();
    }

    private List<Message> getAllMessagesByStatus(MessageStatus status) {
        Criteria criteria = new Criteria(Message.Fields.messageStatus).is(status);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        return elasticsearchOperations.search(criteriaQuery, Message.class).stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
