package it.iks.openapm.unit.actions;

import it.iks.openapm.actions.ActionFactory;
import it.iks.openapm.actions.EmailAction;
import it.iks.openapm.actions.ExecAction;
import it.iks.openapm.actions.SaveAction;
import it.iks.openapm.actions.exceptions.MissingActionException;
import it.iks.openapm.models.ActionConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActionsFactoryTests {
    @Autowired
    protected ActionFactory factory;

    @Test
    public void it_makes_actions_from_config() {
        assertThat(factory.build(new ActionConfig().setType("email"))).isInstanceOf(EmailAction.class);
        assertThat(factory.build(new ActionConfig().setType("exec"))).isInstanceOf(ExecAction.class);
        assertThat(factory.build(new ActionConfig().setType("save"))).isInstanceOf(SaveAction.class);
    }

    @Test(expected = MissingActionException.class)
    public void it_throws_exception_if_action_does_not_exists() {
        factory.build(new ActionConfig().setType("not_found"));
    }
}
