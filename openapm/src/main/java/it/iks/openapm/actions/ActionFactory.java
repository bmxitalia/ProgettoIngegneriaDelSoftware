package it.iks.openapm.actions;

import it.iks.openapm.actions.exceptions.MissingActionException;
import it.iks.openapm.models.ActionConfig;
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
 * Factory to build action instances from configuration
 */
@Component
public class ActionFactory {
    private static final Logger log = LoggerFactory.getLogger(ActionFactory.class);

    /**
     * Table mapping action's type field to concrete action class
     */
    private static final Map<String, Class<? extends Action>> actionsTable;

    static {
        Map<String, Class<? extends Action>> tempActionsTable = new HashMap<>();
        tempActionsTable.put("email", EmailAction.class);
        tempActionsTable.put("exec", ExecAction.class);
        tempActionsTable.put("save", SaveAction.class);
        actionsTable = Collections.unmodifiableMap(tempActionsTable);
    }

    /**
     * Factory to autowire action instances
     */
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    /**
     * Instantiate ActionFactory.
     *
     * @param autowireCapableBeanFactory Factory to autowire action instances
     */
    @Autowired
    public ActionFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    /**
     * Build action from configuration
     *
     * @param config Action configuration
     * @return Concrete Action
     */
    public Action build(ActionConfig config) {
        if (!actionsTable.containsKey(config.getType())) {
            throw new MissingActionException("Cannot find action for config: " + config.toString());
        }

        try {
            Class<? extends Action> actionClass = actionsTable.get(config.getType());
            log.debug("Concrete action: " + actionClass.toString());
            Constructor<? extends Action> constructor = actionClass.getConstructor(ActionConfig.class);

            Action action = constructor.newInstance(config);
            autowireCapableBeanFactory.autowireBean(action);

            return action;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // This should never happen since we are using a reference table
            throw new MissingActionException("Cannot instantiate action for config: " + config.toString(), e);
        }
    }
}
