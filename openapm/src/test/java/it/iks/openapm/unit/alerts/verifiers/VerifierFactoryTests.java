package it.iks.openapm.unit.alerts.verifiers;

import it.iks.openapm.alerts.exceptions.MissingVerifierException;
import it.iks.openapm.alerts.factories.VerifierFactory;
import it.iks.openapm.alerts.verifiers.*;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.models.VerificationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VerifierFactoryTests {
    @Autowired
    protected VerifierFactory factory;

    private AlertConfig config(String verificationOn) {
        AlertConfig config = new AlertConfig();
        config.setVerification(new VerificationConfig(verificationOn, 0));
        return config;
    }

    @Test
    public void it_makes_a_verifier_for_alert() {
        assertThat(factory.forAlert(config("after"))).isInstanceOf(AfterVerifier.class);
        assertThat(factory.forAlert(config("immediately"))).isInstanceOf(ImmediateVerifier.class);
        assertThat(factory.forAlert(config("termination"))).isInstanceOf(TerminationVerifier.class);
    }

    @Test
    public void it_throws_exception_if_verifier_does_not_exists() {
        assertThatExceptionOfType(MissingVerifierException.class)
                .isThrownBy(() -> factory.forAlert(config("not_found")));
    }
}
