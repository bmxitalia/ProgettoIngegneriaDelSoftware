package it.iks.openapm.strategies;

/**
 * Abstract strategy to manage default properties.
 */
abstract public class AbstractStrategy implements Strategy {
    /**
     * Number of data points to retrieve on every strategy.
     */
    protected final int dataPoints;

    /**
     * Build strategy
     *
     * @param dataPoints Number of data points to retrieve on every strategy
     */
    public AbstractStrategy(int dataPoints) {
        this.dataPoints = dataPoints;
    }
}
