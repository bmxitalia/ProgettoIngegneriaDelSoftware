package it.iks.openapm.repositories;

import it.iks.openapm.models.AlertConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository to fetch AlertConfig from Elasticsearch
 */
public interface AlertConfigRepository extends ElasticsearchRepository<AlertConfig, String> {
}
