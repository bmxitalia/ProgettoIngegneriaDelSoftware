package it.iks.openapm.unit.actions;

import it.iks.openapm.actions.SaveAction;
import it.iks.openapm.databases.Database;
import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.ActionConfig;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.templators.IndexTemplator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static it.iks.openapm.TestUtils.parseJson;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SaveActionTests {
    @MockBean
    @Qualifier("actionsDatabase")
    protected Database actionsDatabase;
    @Autowired
    protected IndexTemplator indexTemplator;

    @Test
    public void it_saves_alert_notification() {
        SaveAction action = new SaveAction(new ActionConfig(
                "abc123",
                "action-name",
                "save",
                parseJson("{\"index\": \"alerts_index\",\"type\": \"alerts\"}")
        ));
        action.setActionsDatabase(actionsDatabase);
        action.setIndexTemplator(indexTemplator);

        action.run(new AlertTriggered(new Object(), new AlertConfig().setName("alert-name"), new Date(1514815200000L)));

        Map<String, Object> row = new HashMap<>();
        row.put("name", "alert-name");
        row.put("when", new Date(1514815200000L));
        verify(actionsDatabase).insert(eq("alerts_index"), eq(row), eq("alerts"));
    }
}
