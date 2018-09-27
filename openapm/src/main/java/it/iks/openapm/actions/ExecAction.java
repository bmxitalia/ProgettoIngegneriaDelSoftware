package it.iks.openapm.actions;

import it.iks.openapm.actions.exceptions.ActionConfigurationException;
import it.iks.openapm.actions.exceptions.ActionException;
import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.ActionConfig;
import it.iks.openapm.models.AlertConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * This action execute a given script.
 *
 * Script is executed by creating a temporary file using alert id, action id
 * and current micro-time as name. The content of the "script" field is saved
 * into the file and the file is marked as executable.
 * The file is then executed and at the end it is deleted.
 */
public class ExecAction extends AbstractAction {
    /**
     * {@inheritDoc}
     */
    public ExecAction(ActionConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(AlertTriggered event) {
        File script = createScript(event.getConfig());

        ProcessBuilder builder = new ProcessBuilder(script.getAbsolutePath());

        Map<String, String> env = builder.environment();
        env.put("ALERT_NAME", event.getConfig().getName());
        env.put("ALERT_WHEN", Long.toString(event.getWhen().getTime()));

        try {
            builder.start().waitFor();
        } catch (IOException e) {
            throw new ActionException("Cannot execute action", e);
        } catch (InterruptedException e) {
            throw new ActionException("Cannot wait for process to terminate", e);
        }

        if (!script.delete()) {
            throw new ActionException("Cannot delete script file: " + script.getAbsolutePath());
        }
    }

    private File createScript(AlertConfig alert) {
        String script = script();

        try {
            File file = File.createTempFile(
                    String.format(
                            "%s-%s-%d",
                            config.getId(),
                            alert.getId(),
                            System.currentTimeMillis()
                    ),
                    ""
            );

            try (PrintWriter out = new PrintWriter(file)) {
                out.println(script);
            }

            if (!file.setExecutable(true)) {
                throw new ActionException("Cannot make file executable: " + file.getAbsolutePath());
            }

            return file;
        } catch (IOException e) {
            throw new ActionException("Cannot create script", e);
        }
    }

    private String script() {
        if (!config.getData().containsKey("script") || !(config.getData().get("script") instanceof String)) {
            throw new ActionConfigurationException("Exec action need script data.");
        }

        return (String) config.getData().get("script");
    }
}
