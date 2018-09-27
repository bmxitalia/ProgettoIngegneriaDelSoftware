package it.iks.openapm.unit.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;
import it.iks.openapm.alerts.verifiers.Verifier;
import it.iks.openapm.alerts.factories.VerifierFactory;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.models.VerificationConfig;
import it.iks.openapm.publishers.AlertEventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ImmediateVerifierTests {
    @MockBean
    private AlertEventPublisher publisher;

    @Autowired
    private VerifierFactory factory;

    private AlertConfig config() {
        AlertConfig config = new AlertConfig();
        config.setVerification(new VerificationConfig("immediately", 0));
        return config;
    }

    @Test
    public void it_fires_event_on_active() {
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);
        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);
        verify(publisher, times(1)).triggered(eq(config));
    }

    @Test
    public void it_does_not_fire_event_on_inactive() {
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);
        verifier.onChange(AlertState.INACTIVE, AlertState.ACTIVE);
        verify(publisher, never()).triggered(any());
    }
}
