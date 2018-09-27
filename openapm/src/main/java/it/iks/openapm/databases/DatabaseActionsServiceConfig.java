package it.iks.openapm.databases;

import it.iks.openapm.databases.elasticsearch.ElasticsearchDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides database implementations based on configuration to manage actions.
 *
 * This is used by {@link it.iks.openapm.actions.SaveAction} to insert actions
 * into a database.
 */
@Configuration
public class DatabaseActionsServiceConfig {

    @Value("${app.actions.database.host}")
    private String host;

    @Value("${app.actions.database.port}")
    private int port;

    @Value("${app.actions.database.protocol}")
    private String protocol;

    @Value("${app.actions.database.timestamp}")
    private String timestamp;

    @Value("${app.actions.database.keepalive:60}")
    private int keepalive;

    /**
     * Actions database implementation for Elasticsearch
     *
     * @return Elasticsearch database implementation
     */
    @Bean
    @ConditionalOnProperty(name = "app.actions.database.driver", havingValue = "elasticsearch", matchIfMissing = true)
    public Database actionsDatabase() {
        return new ElasticsearchDatabase(host, port, protocol, timestamp, keepalive);
    }
}
