package it.iks.openapm.databases;

import it.iks.openapm.databases.elasticsearch.ElasticsearchDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides database implementations based on configuration to manage metrics
 */
@Configuration
public class DatabaseMetricsServiceConfig {

    @Value("${app.metrics.database.host}")
    private String host;

    @Value("${app.metrics.database.port}")
    private int port;

    @Value("${app.metrics.database.protocol}")
    private String protocol;

    @Value("${app.metrics.database.timestamp}")
    private String timestamp;

    @Value("${app.metrics.database.keepalive:60}")
    private int keepalive;

    /**
     * Metrics database implementation for Elasticsearch
     *
     * @return Elasticsearch database implementation
     */
    @Bean
    @ConditionalOnProperty(name = "app.metrics.database.driver", havingValue = "elasticsearch", matchIfMissing = true)
    public Database metricsDatabase() {
        return new ElasticsearchDatabase(host, port, protocol, timestamp, keepalive);
    }
}
