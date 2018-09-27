package it.iks.openapm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * A set of static utils for tests
 */
public class TestUtils {
    private static ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Parse a Json string into a Map
     *
     * The item must be an Object and not an Array.
     * This function is meant to be used to simplify
     * complex items creation in tests' mocks/inputs.
     *
     * @param json Json code
     * @return Parsed element
     */
    public static Map<String, Object> parseJson(String json) {
        try {
            return jsonMapper.readValue(json, new MapTypeReference());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MapTypeReference extends TypeReference<Map<String, Object>> {

    }
}
