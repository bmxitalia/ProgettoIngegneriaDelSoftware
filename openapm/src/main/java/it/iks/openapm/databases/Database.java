package it.iks.openapm.databases;

import java.util.Date;
import java.util.Map;

/**
 * Database abstraction to fetch/push data to/from databases
 */
public interface Database {
    /**
     * Find all items in given timing interval
     *
     * @param index Index/Table name to fetch from
     * @param from  Initial time (inclusive)
     * @param to    Final time (exclusive)
     * @return Iterable of results
     */
    Iterable<Map<String, Object>> findAllInInterval(String index, Date from, Date to);

    /**
     * Find items in reverse chronological order (newest first) matching the given conditions
     *
     * @param index      Index/Table name to fetch from
     * @param conditions List of conditions in the form attribute-value
     * @return Resulting item
     */
    Iterable<Map<String, Object>> findNewestWhere(String index, Map<String, Object> conditions);

    /**
     * Insert a new object into the database
     *
     * @param index  Index/Table name to insert into
     * @param object Object attributes
     * @param type   Object type for engines that needs it
     */
    void insert(String index, Map<String, Object> object, String type);
}
