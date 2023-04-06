package com.intern.practice.repository.impl;

import com.intern.practice.entity.Message;
import com.intern.practice.entity.MessageStatus;
import com.intern.practice.repository.MessageRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class MessageRepoImpl implements MessageRepo {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Optional<Message> findById(String id) {
        return Optional.ofNullable(elasticsearchOperations.get(id, Message.class));
    }

    @Override
    public List<Message> read() {
        Criteria criteria = new Criteria(Message.Fields.messageStatus)
                .is(MessageStatus.ERROR)
                .or(Message.Fields.messageStatus)
                .is(MessageStatus.PENDING);
        Query query = new CriteriaQuery(criteria);

        SearchHits<Message> searchHits = elasticsearchOperations.search(query, Message.class);

        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public String save(Message message) {
        return elasticsearchOperations.save(message).getId();
    }
}
