package it.iks.openapm.unit.templators;

import it.iks.openapm.templators.IndexTemplator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexTemplatorTests {
    @Autowired
    private IndexTemplator templator;

    @Test
    public void it_fetch_static_values() {
        assertThat(templator.value("static_value")).isEqualTo("static_value");
    }

    @Test
    public void it_process_date_template() {
        assertThat(templator.value("#'template-value-'yyyy.MM.dd"))
                .isEqualTo(String.format(
                        "template-value-%04d.%02d.%02d",
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getDayOfMonth()
                ));
    }

    @Test
    public void it_process_date_template_with_given_parameter() {
        assertThat(templator.value("#'template-value-'yyyy.MM.dd", new Date(1514764800000L)))
                .isEqualTo("template-value-2018.01.01");
    }
}
