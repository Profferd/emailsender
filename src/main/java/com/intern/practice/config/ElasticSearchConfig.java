package com.intern.practice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value(value = "${elasticsearch.address}")
    private String eAddress;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(eAddress)
                .build();
    }
}
