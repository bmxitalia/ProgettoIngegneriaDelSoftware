package it.iks.openapm.alerts.factories;

import it.iks.openapm.alerts.Alert;
import it.iks.openapm.alerts.BaselineRetriever;
import it.iks.openapm.alerts.Condition;
import it.iks.openapm.alerts.MetricState;
import it.iks.openapm.databases.Database;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.models.BaselineValue;
import it.iks.openapm.models.ConditionConfig;
import it.iks.openapm.models.Operation;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.strategies.StrategyFactory;
import it.iks.openapm.templators.IndexTemplator;
import it.iks.openapm.utils.Debouncer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory to alert alerts from configuration
 */
@Component
public class AlertFactory {
    private final Database baselinesDatabase;
    private final Debouncer debouncer;
    private final IndexTemplator indexTemplator;
    private final EvaluatorFactory evaluatorFactory;
    private final OperatorFactory operatorFactory;
    private final StrategyFactory strategyFactory;
    private final VerifierFactory verifierFactory;

    @Value("${app.alerts.evaluate.debounce}")
    private int evaluateDebounce;

    @Autowired
    public AlertFactory(Database baselinesDatabase,
                        Debouncer debouncer,
                        IndexTemplator indexTemplator,
                        EvaluatorFactory evaluatorFactory,
                        OperatorFactory operatorFactory,
                        StrategyFactory strategyFactory,
                        VerifierFactory verifierFactory) {
        this.baselinesDatabase = baselinesDatabase;
        this.debouncer = debouncer;
        this.indexTemplator = indexTemplator;
        this.evaluatorFactory = evaluatorFactory;
        this.operatorFactory = operatorFactory;
        this.strategyFactory = strategyFactory;
        this.verifierFactory = verifierFactory;
    }

    /**
     * Build a new alert from config
     *
     * @param config Alert configuration
     * @return Alert instance
     */
    public Alert alert(AlertConfig config) {
        return new Alert(
                config,
                buildConditions(config.getConditions()),
                evaluatorFactory.byIdentifier(config.getEvaluateWhen()),
                verifierFactory.forAlert(config),
                debouncer,
                evaluateDebounce
        );
    }

    private List<Condition> buildConditions(ConditionConfig[] configs) {
        return Collections.unmodifiableList(
                Arrays.stream(configs)
                        .map(this::condition)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Build a new condition from configuration
     *
     * @param config Condition configuration
     * @return Condition instance
     */
    public Condition condition(ConditionConfig config) {
        return new Condition(
                buildMetricStates(config.getMetrics()),
                operatorFactory.byModel(config.getCalculation()),
                baselineRetriever(config.getBaseline()),
                operatorFactory.byModel(config.getEvaluation())
        );
    }

    private MetricState[] buildMetricStates(Operation[][] configs) {
        return Arrays.stream(configs)
                .map(this::metricState)
                .toArray(MetricState[]::new);
    }

    /**
     * Build a metric state from filters
     *
     * @param filters Metric filters
     * @return Metric state
     */
    public MetricState metricState(Operation[] filters) {
        return new MetricState(
                Arrays.stream(filters)
                        .map(operatorFactory::byModel)
                        .toArray(Operator[]::new)
        );
    }

    /**
     * Build a baseline retriever from config
     *
     * @param config Baseline retriever configuration
     * @return Baseline retriever if config is different from null, otherwise null
     */
    public BaselineRetriever baselineRetriever(BaselineValue config) {
        if (config == null) {
            return null;
        }

        return new BaselineRetriever(
                config,
                baselinesDatabase,
                indexTemplator,
                Arrays.stream(config.getFilters())
                        .map(operatorFactory::byModel)
                        .toArray(Operator[]::new),
                strategyFactory.byIdentifier(config.getStrategy()),
                operatorFactory.byModel(config.getCalculation())
        );
    }
}
