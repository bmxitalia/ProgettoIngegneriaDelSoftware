package it.iks.openapm.templators;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Templator to manage index names.
 */
@Component
public class IndexTemplator {
    /**
     * Grab the index identifier and process template.
     *
     * @param identifier Index identifier to be parsed
     * @return Actual value
     */
    public String value(String identifier) {
        return value(identifier, new Date());
    }

    /**
     * Grab the index identifier and process template.
     *
     * @param identifier Index identifier to be parsed
     * @param when       Context time for current job
     * @return Actual value
     */
    public String value(String identifier, Date when) {
        if (isTemplate(identifier)) {
            return processTemplate(identifier, when);
        }

        return identifier;
    }

    /**
     * Determine if identifier is a template.
     *
     * An identifier is a template if it starts with "#".
     *
     * @param identifier Index identifier to be parsed
     * @return True if it is a template
     */
    private boolean isTemplate(String identifier) {
        return identifier.startsWith("#");
    }

    /**
     * Process template into actual value.
     *
     * Remove the beginning "#" and go throw SimpleDateFormat with
     * current time.
     *
     * @param identifier Index identifier to be parsed
     * @return Actual value
     */
    private String processTemplate(String identifier, Date when) {
        return new SimpleDateFormat(identifier.substring(1)).format(when);
    }
}
