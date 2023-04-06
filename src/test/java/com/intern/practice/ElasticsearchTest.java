package com.intern.practice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;

@TestConfiguration
public class ElasticsearchTest extends ElasticsearchConfiguration {

    @Value("${elastic.test-container}")
    private String elasticContainerAddress;

    @Bean(destroyMethod = "stop")
    public ElasticsearchContainer elasticsearchContainer() {
        ElasticsearchContainer container = new ElasticsearchContainer(
                elasticContainerAddress);
        container.setEnv(List.of(
                "discovery.type=single-node",
                "ES_JAVA_OPTS=-Xms1g -Xmx1g",
                "xpack.security.enabled=false")
        );
        container.start();
        return container;
    }

    @Bean
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchContainer().getHttpHostAddress())
                .build();
    }
}