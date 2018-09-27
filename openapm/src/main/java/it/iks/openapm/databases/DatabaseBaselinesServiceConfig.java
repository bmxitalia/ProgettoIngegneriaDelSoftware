package it.iks.openapm.databases;

import it.iks.openapm.databases.elasticsearch.ElasticsearchDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides database implementations based on configuration to manage baselines
 */
@Configuration
public class DatabaseBaselinesServiceConfig {

    @Value("${app.baselines.database.host}")
    private String host;

    @Value("${app.baselines.database.port}")
    private int port;

    @Value("${app.baselines.database.protocol}")
    private String protocol;

    @Value("${app.baselines.database.timestamp}")
    private String timestamp;

    @Value("${app.baselines.database.keepalive:60}")
    private int keepalive;

    /**
     * Baselines database implementation for Elasticsearch
     *
     * @return Elasticsearch database implementation
     */
    @Bean
    @ConditionalOnProperty(name = "app.baselines.database.driver", havingValue = "elasticsearch", matchIfMissing = true)
    public Database baselinesDatabase() {
        return new ElasticsearchDatabase(host, port, protocol, timestamp, keepalive);
    }
}
