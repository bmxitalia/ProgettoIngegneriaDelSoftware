package it.iks.openapm.repositories;

import it.iks.openapm.models.MetricConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository to fetch MetricConfig from Elasticsearch
 */
public interface MetricConfigRepository extends ElasticsearchRepository<MetricConfig, String> {
}
