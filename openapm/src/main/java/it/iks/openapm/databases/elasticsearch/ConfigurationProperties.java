package it.iks.openapm.databases.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bind application properties to IoC Beans
 *
 * This is needed to resolve properties in annotations.
 * Example: @Document(indexName = "#{@metricsConfigIndex}", type = "#{@metricsConfigType}")
 *
 * TODO: Find a more elegant and less verbose annotation binding way
 */
@Configuration
public class ConfigurationProperties {
    /**
     * Elasticsearch index to store metric generation configurations
     */
    @Value("${app.metrics.config.index}")
    private String metricsConfigIndex;

    /**
     * Elasticsearch type for metric configurations
     */
    @Value("${app.metrics.config.type}")
    private String metricsConfigType;

    /**
     * Elasticsearch index to store baseline generation configurations
     */
    @Value("${app.baselines.config.index}")
    private String baselinesConfigIndex;

    /**
     * Elasticsearch type for baseline configurations
     */
    @Value("${app.baselines.config.type}")
    private String baselinesConfigType;

    /**
     * Elasticsearch index to store alert configurations
     */
    @Value("${app.alerts.config.index}")
    private String alertsConfigIndex;

    /**
     * Elasticsearch type for alert configurations
     */
    @Value("${app.alerts.config.type}")
    private String alertsConfigType;

    /**
     * Elasticsearch index to store action configurations
     */
    @Value("${app.actions.config.index}")
    private String actionsConfigIndex;

    /**
     * Elasticsearch type for action configurations
     */
    @Value("${app.actions.config.type}")
    private String actionsConfigType;

    /////////////////////////////// BEANS FROM PROPERTIES ///////////////////////////////

    @Bean
    public String metricsConfigIndex() {
        return metricsConfigIndex;
    }

    @Bean
    public String metricsConfigType() {
        return metricsConfigType;
    }

    @Bean
    public String baselinesConfigIndex() {
        return baselinesConfigIndex;
    }

    @Bean
    public String baselinesConfigType() {
        return baselinesConfigType;
    }

    @Bean
    public String alertsConfigIndex() {
        return alertsConfigIndex;
    }

    @Bean
    public String alertsConfigType() {
        return alertsConfigType;
    }

    @Bean
    public String actionsConfigIndex() {
        return actionsConfigIndex;
    }

    @Bean
    public String actionsConfigType() {
        return actionsConfigType;
    }
}
