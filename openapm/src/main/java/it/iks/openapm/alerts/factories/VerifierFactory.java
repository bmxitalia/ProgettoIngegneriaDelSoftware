package it.iks.openapm.alerts.factories;

import it.iks.openapm.alerts.exceptions.MissingVerifierException;
import it.iks.openapm.alerts.verifiers.AfterVerifier;
import it.iks.openapm.alerts.verifiers.ImmediateVerifier;
import it.iks.openapm.alerts.verifiers.TerminationVerifier;
import it.iks.openapm.alerts.verifiers.Verifier;
import it.iks.openapm.models.AlertConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to alert Verifier objects.
 */
@Component
public class VerifierFactory {
    private static final Logger log = LoggerFactory.getLogger(VerifierFactory.class);

    /**
     * Table mapping verifier on field to concrete verifier class
     */
    private static final Map<String, Class<? extends Verifier>> verifiersTable;

    static {
        Map<String, Class<? extends Verifier>> tempVerifiersTable = new HashMap<>();
        tempVerifiersTable.put("after", AfterVerifier.class);
        tempVerifiersTable.put("immediately", ImmediateVerifier.class);
        tempVerifiersTable.put("termination", TerminationVerifier.class);
        verifiersTable = Collections.unmodifiableMap(tempVerifiersTable);
    }

    /**
     * Factory to autowire verifier instance
     */
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    /**
     * Instantiate VerifierFactory.
     *
     * @param autowireCapableBeanFactory Factory to autowire verifier instance
     */
    @Autowired
    public VerifierFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    /**
     * Build verifier for given alert
     *
     * @param config Alert configuration
     * @return Concrete Verifier
     */
    public Verifier forAlert(AlertConfig config) {
        if (!verifiersTable.containsKey(config.getVerification().getOn())) {
            throw new MissingVerifierException(String.format("Cannot find verifier \"%s\"", config.getVerification().getOn()));
        }

        try {
            Class<? extends Verifier> verifierClass = verifiersTable.get(config.getVerification().getOn());
            log.debug("Concrete verifier: " + verifierClass.toString());
            Constructor<? extends Verifier> constructor = verifierClass.getConstructor(AlertConfig.class);

            Verifier verifier = constructor.newInstance(config);
            autowireCapableBeanFactory.autowireBean(verifier);

            return verifier;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // This should never happen since we are using a reference table
            throw new MissingVerifierException(String.format("Cannot instantiate verifier \"%s\"", config.getVerification().getOn()), e);
        }
    }
}
