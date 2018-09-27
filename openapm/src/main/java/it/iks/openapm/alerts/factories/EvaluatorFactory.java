package it.iks.openapm.alerts.factories;

import it.iks.openapm.alerts.evaluators.AllMatchEvaluator;
import it.iks.openapm.alerts.evaluators.AnyMatchEvaluator;
import it.iks.openapm.alerts.evaluators.Evaluator;
import it.iks.openapm.alerts.exceptions.MissingEvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to alert Evaluator objects.
 */
@Component
public class EvaluatorFactory {
    private static final Logger log = LoggerFactory.getLogger(EvaluatorFactory.class);

    /**
     * Table mapping evaluator field to concrete evaluator class
     */
    private static final Map<String, Class<? extends Evaluator>> evaluatorsTable;

    static {
        Map<String, Class<? extends Evaluator>> tempEvaluatorsTable = new HashMap<>();
        tempEvaluatorsTable.put("all_match", AllMatchEvaluator.class);
        tempEvaluatorsTable.put("any_match", AnyMatchEvaluator.class);
        evaluatorsTable = Collections.unmodifiableMap(tempEvaluatorsTable);
    }

    /**
     * Build evaluator using its identifier
     *
     * @param identifier Evaluator identifier
     * @return Concrete Evaluator
     */
    public Evaluator byIdentifier(String identifier) {
        if (!evaluatorsTable.containsKey(identifier)) {
            throw new MissingEvaluatorException(String.format("Cannot find evaluator \"%s\"", identifier));
        }

        try {
            Class<? extends Evaluator> evaluatorClass = evaluatorsTable.get(identifier);

            log.debug("Concrete evaluator: " + evaluatorClass.toString());

            Constructor<? extends Evaluator> constructor = evaluatorClass.getConstructor();

            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // This should never happen since we are using a reference table
            throw new MissingEvaluatorException(String.format("Cannot instantiate evaluator \"%s\"", identifier), e);
        }
    }
}
