package it.iks.openapm.actions;

import it.iks.openapm.actions.exceptions.ActionConfigurationException;
import it.iks.openapm.databases.Database;
import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.ActionConfig;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * This actions saves an "alert notification" into given database
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class SaveAction extends AbstractAction {
    private Database actionsDatabase;
    private IndexTemplator indexTemplator;

    /**
     * {@inheritDoc}
     */
    public SaveAction(ActionConfig config) {
        super(config);
    }

    @Autowired
    public void setActionsDatabase(Database actionsDatabase) {
        this.actionsDatabase = actionsDatabase;
    }

    @Autowired
    public void setIndexTemplator(IndexTemplator indexTemplator) {
        this.indexTemplator = indexTemplator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(AlertTriggered event) {
        actionsDatabase.insert(index(), build(event), type());
    }

    private Map<String, Object> build(AlertTriggered event) {
        Map<String, Object> action = new HashMap<>();
        action.put("name", event.getConfig().getName());
        action.put("when", event.getWhen());
        return action;
    }

    private String index() {
        if (!config.getData().containsKey("index") || !(config.getData().get("index") instanceof String)) {
            throw new ActionConfigurationException("Save action need index data.");
        }

        return indexTemplator.value((String) config.getData().get("index"));
    }

    private String type() {
        if (!config.getData().containsKey("type") || !(config.getData().get("type") instanceof String)) {
            throw new ActionConfigurationException("Save action need type data.");
        }

        return (String) config.getData().get("type");
    }
}
