package it.iks.openapm.unit.alerts;

import it.iks.openapm.alerts.AlertsManager;
import it.iks.openapm.alerts.factories.AlertFactory;
import it.iks.openapm.repositories.AlertConfigRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AlertsManagerTests {
    @MockBean
    @Qualifier("alertFactory")
    private AlertFactory alertFactory;
    @MockBean
    @Qualifier("alertConfigRepository")
    private AlertConfigRepository alertRepository;

    @Test
    public void it_does_not_fails_if_one_alert_creation_fails() {
        given(alertRepository.findAll()).willReturn(Arrays.asList(null, null, null));
        given(alertFactory.alert(any()))
                .willReturn(null)
                .willThrow(new RuntimeException("Alert creation is not encapsulated."))
                .willReturn(null);

        AlertsManager manager = new AlertsManager(alertRepository, alertFactory);
        manager.postConstructor();

        // No exceptions! Yeah!
    }
}
