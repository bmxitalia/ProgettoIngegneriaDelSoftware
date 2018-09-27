package it.iks.openapm.unit.actions;

import it.iks.openapm.actions.EmailAction;
import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.ActionConfig;
import it.iks.openapm.models.AlertConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import static it.iks.openapm.TestUtils.parseJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailActionTests {
    @MockBean
    protected JavaMailSender sender;

    @Test
    public void it_sends_email() throws Exception {
        ArgumentCaptor<MimeMessagePreparator> preparatorCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        EmailAction action = new EmailAction(new ActionConfig(
                "abc123",
                "action-name",
                "email",
                parseJson("{\"to\": \"alert@example.com\",\"subject\": \"Alert {{ name }}\",\"body\": \"Alert <b>{{ name }}</b> has been triggered at <i>{{ when | date('yyyy.MM.dd HH:mm:ss') }}</i>.\"}")
        ));
        action.setSender(sender);

        action.run(new AlertTriggered(new Object(), new AlertConfig().setName("alert-name"), new Date(1514815200000L)));

        verify(sender).send(preparatorCaptor.capture());
        MimeMessagePreparator preparator = preparatorCaptor.getValue();
        MimeMessage message = new MimeMessage((Session) null);
        preparator.prepare(message);
        assertThat(message.getRecipients(Message.RecipientType.TO).length).isEqualTo(1);
        assertThat(message.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo("alert@example.com");
        assertThat(message.getSubject()).isEqualTo("Alert alert-name");
        assertThat(message.getContent()).isEqualTo("Alert <b>alert-name</b> has been triggered at <i>2018.01.01 15:00:00</i>.");
    }
}
