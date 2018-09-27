package it.iks.openapm.repositories;

import it.iks.openapm.models.ActionConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository to fetch ActionConfig from Elasticsearch
 */
public interface ActionConfigRepository extends ElasticsearchRepository<ActionConfig, String> {
}
