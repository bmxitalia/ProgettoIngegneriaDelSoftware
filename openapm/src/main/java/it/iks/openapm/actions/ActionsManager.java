package it.iks.openapm.actions;

import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.repositories.ActionConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Manage remediation actions execution from alerts triggering
 */
@Component
public class ActionsManager implements ApplicationListener<AlertTriggered> {
    private static final Logger log = LoggerFactory.getLogger(ActionsManager.class);

    private final ActionConfigRepository repository;
    private final ActionFactory factory;

    @Autowired
    public ActionsManager(ActionConfigRepository repository, ActionFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    /**
     * Receive an AlertTriggered and execute every remediation action
     *
     * @param event AlertTriggered
     */
    @Override
    public void onApplicationEvent(AlertTriggered event) {
        log.debug(event.getConfig().toString());

        repository
                .findAllById(Arrays.asList(event.getConfig().getActions()))
                .forEach((config -> {
                    try {
                        log.debug(config.toString());

                        factory.build(config).run(event);
                    } catch (Exception e) {
                        log.warn("Exception while executing action: " + config, e);
                    }
                }));
    }
}
