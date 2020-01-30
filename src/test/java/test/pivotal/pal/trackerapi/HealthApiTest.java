package test.pivotal.pal.trackerapi;

import com.jayway.jsonpath.DocumentContext;
import io.pivotal.pal.tracker.Application;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
public class HealthApiTest {

    @LocalServerPort
    private String port;
   private TestRestTemplate restTemplate;



    @BeforeEach
    public void setUp() throws Exception {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port)
                .basicAuthentication("user", "password");

        restTemplate = new TestRestTemplate(builder);
    }

    @Test
    public void healthTest() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/actuator/health", String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext healthJson = parse(response.getBody());

        assertThat(healthJson.read("$.status", String.class)).isEqualTo("UP");
        assertThat(healthJson.read("$.components.db.status", String.class)).isEqualTo("UP");
        assertThat(healthJson.read("$.components.diskSpace.status", String.class)).isEqualTo("UP");
    }
}