package it.iks.openapm.databases.elasticsearch;

import it.iks.openapm.databases.DatabaseException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Iterator to complete a given Elasticsearch Request using Scroll API
 */
public class ElasticsearchRequestIterator implements Iterator<Map<String, Object>> {
    private RestHighLevelClient client;
    private SearchResponse response;
    private String scrollId;
    private TimeValue scrollKeepAlive;

    private SearchHit[] hits;
    private int index = 0;
    private boolean isEnd = false;

    /**
     * Build iterator for given
     *
     * @param client          Elasticsearch client
     * @param response        First bulk
     * @param scrollKeepAlive Scroll request keep alive time
     */
    public ElasticsearchRequestIterator(RestHighLevelClient client, SearchResponse response, TimeValue scrollKeepAlive) {
        this.client = client;
        this.response = response;
        this.scrollId = response.getScrollId();
        this.scrollKeepAlive = scrollKeepAlive;
        this.hits = retrieveHits();
    }

    @Override
    public boolean hasNext() {
        if (index < hits.length) {
            return true;
        }

        if (isEnd) {
            // All available requests have been made
            return false;
        }

        try {
            fetchNextBulk();
        } catch (IOException e) {
            throw new DatabaseException("Cannot fetch next bulk from Elasticsearch", e);
        }

        return hits.length > 0;
    }

    @Override
    public Map<String, Object> next() {
        if (!hasNext()) {
            return null;
        }

        return hits[index++].getSourceAsMap();
    }

    /**
     * Fetch next set of documents
     *
     * @throws IOException Cannot communicate with Elasticsearch
     */
    private void fetchNextBulk() throws IOException {
        response = client.searchScroll(new SearchScrollRequest(scrollId).scroll(scrollKeepAlive));
        index = 0;
        hits = retrieveHits();

        if (hits.length <= 0) {
            // No documents returned, so we are at the end of the scrolls
            isEnd = true;
        }
    }

    /**
     * Retrieve hits from current response
     *
     * @return Array of hits
     */
    private SearchHit[] retrieveHits() {
        return response.getHits().getHits();
    }
}
