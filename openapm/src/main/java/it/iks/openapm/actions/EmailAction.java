package it.iks.openapm.actions;

import it.iks.openapm.actions.exceptions.ActionConfigurationException;
import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.ActionConfig;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;

/**
 * This action send a custom email.
 *
 * The following data will be fetched from the configuration:
 * - to: Destination address
 * - subject: Subject of the email
 * - body: Template of email
 *
 * Subject and body will be processed using jTwig {@see http://jtwig.org}.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class EmailAction extends AbstractAction {
    private JavaMailSender sender;

    /**
     * {@inheritDoc}
     */
    public EmailAction(ActionConfig config) {
        super(config);
    }

    @Autowired
    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(AlertTriggered event) {
        JtwigModel model = data(event);

        sender.send(message -> {
            message.setRecipients(Message.RecipientType.TO, to());
            message.setSubject(subject(model));
            message.setContent(body(model), "text/html");
        });
    }

    private JtwigModel data(AlertTriggered event) {
        return JtwigModel.newModel()
                .with("name", event.getConfig().getName())
                .with("when", event.getWhen());
    }

    private String to() {
        if (!config.getData().containsKey("to") || !(config.getData().get("to") instanceof String)) {
            throw new ActionConfigurationException("Email action needs a destination address.");
        }

        return (String) config.getData().get("to");
    }

    private String subject(JtwigModel model) {
        if (!config.getData().containsKey("subject") || !(config.getData().get("subject") instanceof String)) {
            throw new ActionConfigurationException("Email action needs subject template.");
        }

        return JtwigTemplate.inlineTemplate((String) config.getData().get("subject")).render(model);
    }

    private String body(JtwigModel model) {
        if (!config.getData().containsKey("body") || !(config.getData().get("body") instanceof String)) {
            throw new ActionConfigurationException("Email action needs body template.");
        }

        return JtwigTemplate.inlineTemplate((String) config.getData().get("body")).render(model);
    }
}
