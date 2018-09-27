package it.iks.openapm.actions;

import it.iks.openapm.models.ActionConfig;

/**
 * Abstract action to manage common features
 */
abstract public class AbstractAction implements Action {
    protected final ActionConfig config;

    /**
     * Instantiate AbstractAction.
     *
     * @param config Action configuration
     */
    protected AbstractAction(ActionConfig config) {
        this.config = config;
    }
}
