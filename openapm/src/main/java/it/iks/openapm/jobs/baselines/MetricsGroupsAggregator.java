package it.iks.openapm.jobs.baselines;

import it.iks.openapm.models.contracts.AggregableConfig;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Aggregate all metrics' groups into groups defined by "aggregator" operation in config
 *
 * The outer groups will be the configured aggregations, and the inner groups will be
 * the initial groups (periods). This will allow the calculator to pick one outer-group
 * at a time and calculate the correlated baseline.
 *
 * -- Example
 * Input:
 * [
 * period1 = [groupA, groupB, groupA, groupC],
 * period2 = [groupB, groupB, groupA, groupC],
 * period3 = [groupA, groupB, groupC, groupC]
 * ]
 *
 * Output:
 * {
 * groupA = [period1=[..], period2=[..], period3=[..]],
 * groupB = [period1=[..], period2=[..], period3=[..]],
 * groupC = [period1=[..], period2=[..], period3=[..]]
 * }
 */
public class MetricsGroupsAggregator implements ItemProcessor<Iterable<Iterable<Map<String, Object>>>, Map<String, List<List<Map<String, Object>>>>> {
    /**
     * Calculation config to fetch aggregator
     */
    private final AggregableConfig config;

    /**
     * Operator factory to alert aggregator from Operation model
     */
    private final OperatorFactory operatorFactory;

    /**
     * Initialize MetricsGroupsAggregator
     *
     * @param config          Calculation config to fetch aggregator
     * @param operatorFactory Operator factory to alert aggregator from Operation model
     */
    public MetricsGroupsAggregator(AggregableConfig config, OperatorFactory operatorFactory) {
        this.config = config;
        this.operatorFactory = operatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<List<Map<String, Object>>>> process(Iterable<Iterable<Map<String, Object>>> periods) {
        return reverseGroups(groupEveryPeriod(periods));
    }

    /**
     * Apply group operation on every period
     *
     * -- Example
     * Input:
     * [
     * period1 = [groupA, groupB, groupA, groupC],
     * period2 = [groupB, groupB, groupA, groupC],
     * period3 = [groupA, groupB, groupC, groupC]
     * ]
     *
     * Output:
     * [
     * period1 = {groupA=[..], groupB=[..], groupC=[..]},
     * period2 = {groupA=[..], groupB=[..], groupC=[..]},
     * period3 = {groupA=[..], groupB=[..], groupC=[..]}
     * ]
     *
     * @param periods Iterable of not-grouped periods
     * @return List of periods with metrics grouped using aggregator
     */
    private List<Map<String, List<Map<String, Object>>>> groupEveryPeriod(Iterable<Iterable<Map<String, Object>>> periods) {
        Operator aggregator = operatorFactory.byModel(config.getAggregation());
        List<Map<String, List<Map<String, Object>>>> groupedPeriods = new ArrayList<>();

        for (Iterable<Map<String, Object>> period : periods) {
            groupedPeriods.add(StreamSupport.stream(period.spliterator(), false)
                    .collect(Collectors.groupingBy(aggregator::group)));
        }

        return groupedPeriods;
    }

    /**
     * Reverse group and period organization to have groups outside
     *
     * -- Example
     * Input:
     * [
     * period1 = {groupA=[..], groupB=[..], groupC=[..]},
     * period2 = {groupA=[..], groupB=[..], groupC=[..]},
     * period3 = {groupA=[..], groupB=[..], groupC=[..]}
     * ]
     *
     * Output:
     * {
     * groupA = [period1=[..], period2=[..], period3=[..]],
     * groupB = [period1=[..], period2=[..], period3=[..]],
     * groupC = [period1=[..], period2=[..], period3=[..]]
     * }
     *
     * @param groupedPeriods Periods outside, groups inside
     * @return Groups outside, periods inside
     */
    private Map<String, List<List<Map<String, Object>>>> reverseGroups(List<Map<String, List<Map<String, Object>>>> groupedPeriods) {
        Map<String, List<List<Map<String, Object>>>> groups = new HashMap<>();

        for (Map<String, List<Map<String, Object>>> groupedPeriod : groupedPeriods) {
            for (String key : groupedPeriod.keySet()) {
                groups.putIfAbsent(key, new ArrayList<>());
                groups.get(key).add(groupedPeriod.get(key));
            }
        }

        return groups;
    }
}
