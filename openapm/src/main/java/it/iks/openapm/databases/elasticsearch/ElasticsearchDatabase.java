package it.iks.openapm.databases.elasticsearch;

import it.iks.openapm.databases.Database;
import it.iks.openapm.databases.DatabaseException;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Elasticsearch implementation of Database
 */
public class ElasticsearchDatabase implements Database {
    private RestHighLevelClient client;

    /**
     * Timestamp field name to use to fetch and insert records
     */
    private String timestamp;

    /**
     * Keep alive time for Scroll APIs in seconds
     */
    private int keepalive;

    /**
     * Initialize a new database instance
     */
    public ElasticsearchDatabase(String host, int port, String protocol, String timestamp, int keepalive) {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, protocol)).build()
        );
        this.timestamp = timestamp;
        this.keepalive = keepalive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Map<String, Object>> findAllInInterval(String index, Date from, Date to) {
        return new ElasticsearchRequest(
                client,
                buildSearchRequest(index, from, to),
                new TimeValue(keepalive, TimeUnit.SECONDS)
        );
    }

    /**
     * Build a search request object
     *
     * @param index Index/Table name to fetch from
     * @param from  Initial time (inclusive)
     * @param to    Final time (exclusive)
     * @return SearchRequest
     */
    private SearchRequest buildSearchRequest(String index, Date from, Date to) {
        return new SearchRequest(index)
                .source(buildSearchSourceBuilder(from, to));
    }

    /**
     * Build a search source builder filtering by the given time range
     *
     * Document order is not needed for results, to make use of scroll requests
     * optimizations we sort the documents by _doc. For more info
     * {@see https://www.elastic.co/guide/en/elasticsearch/reference/6.2/search-request-scroll.html}
     *
     * @param from Initial time (inclusive)
     * @param to   Final time (exclusive)
     * @return SearchSourceBuilder
     */
    private SearchSourceBuilder buildSearchSourceBuilder(Date from, Date to) {
        return new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery(timestamp).from(from).to(to))
                .sort(new FieldSortBuilder("_doc").order(SortOrder.ASC));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Map<String, Object>> findNewestWhere(String index, Map<String, Object> conditions) {
        return new ElasticsearchRequest(
                client,
                buildNewestWhereRequest(index, conditions),
                new TimeValue(keepalive, TimeUnit.SECONDS)
        );
    }

    /**
     * Build a search query matching all items with conditions sort by timestamp (newest first)
     *
     * @param index      Index/Table name to fetch from
     * @param conditions List of conditions in the form attribute-value
     * @return SearchRequest
     */
    private SearchRequest buildNewestWhereRequest(String index, Map<String, Object> conditions) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for (String key : conditions.keySet()) {
            query.must(QueryBuilders.termQuery(key, conditions.get(key)));
        }

        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(query)
                .sort(new FieldSortBuilder(timestamp).order(SortOrder.DESC));

        return new SearchRequest(index)
                .source(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(String index, Map<String, Object> object, String type) {
        object.put(timestamp, new Date());

        IndexRequest request = new IndexRequest(index, type)
                .source(object);

        try {
            client.index(request);
        } catch (IOException e) {
            throw new DatabaseException(String.format(
                    "Cannot insert into \"%s\" object: %s",
                    index,
                    Arrays.toString(object.entrySet().toArray())
            ), e);
        }
    }
}
