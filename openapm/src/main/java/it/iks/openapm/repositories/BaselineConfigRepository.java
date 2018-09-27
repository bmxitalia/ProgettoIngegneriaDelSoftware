package it.iks.openapm.repositories;

import it.iks.openapm.models.BaselineConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository to fetch BaselineConfig from Elasticsearch
 */
public interface BaselineConfigRepository extends ElasticsearchRepository<BaselineConfig, String> {
}
