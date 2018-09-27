package it.iks.openapm.databases.elasticsearch;

import it.iks.openapm.databases.DatabaseException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Iterable to make an Elasticsearch request using Scroll API
 *
 * Warning: Every generation of an Iterator makes a new request, use carefully
 */
public class ElasticsearchRequest implements Iterable<Map<String, Object>> {
    private RestHighLevelClient client;

    private SearchRequest request;
    private TimeValue scrollKeepAlive;

    /**
     * Create a new request object
     *
     * @param client          Elasticsearch client
     * @param request         Prepared Elasticsearch request
     * @param scrollKeepAlive Scroll request keep alive time
     */
    public ElasticsearchRequest(RestHighLevelClient client, SearchRequest request, TimeValue scrollKeepAlive) {
        this.client = client;
        this.request = request;
        this.scrollKeepAlive = scrollKeepAlive;
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        try {
            return new ElasticsearchRequestIterator(client, fetchFirstBulk(), scrollKeepAlive);
        } catch (IOException e) {
            throw new DatabaseException("Cannot alert ElasticsearchRequestIterator", e);
        }
    }

    /**
     * Make the first request using given settings and keep alive time
     *
     * @return First bulk
     * @throws IOException Failed to communicate with Elasticsearch
     */
    private SearchResponse fetchFirstBulk() throws IOException {
        return client.search(request.scroll(scrollKeepAlive));
    }
}
