package it.iks.openapm.databases;

import it.iks.openapm.databases.elasticsearch.ElasticsearchDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides database implementations based on configuration to manage traces
 */
@Configuration
public class DatabaseTracesServiceConfig {

    @Value("${app.traces.database.host}")
    private String host;

    @Value("${app.traces.database.port}")
    private int port;

    @Value("${app.traces.database.protocol}")
    private String protocol;

    @Value("${app.traces.database.timestamp}")
    private String timestamp;

    @Value("${app.traces.database.keepalive:60}")
    private int keepalive;

    /**
     * traces database implementation for Elasticsearch
     *
     * @return Elasticsearch database implementation
     */
    @Bean
    @ConditionalOnProperty(name = "app.traces.database.driver", havingValue = "elasticsearch", matchIfMissing = true)
    public Database tracesDatabase() {
        return new ElasticsearchDatabase(host, port, protocol, timestamp, keepalive);
    }
}
