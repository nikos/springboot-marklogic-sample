package de.nava.mlsample.web;

import de.nava.mlsample.MarkLogicSampleApplication;
import de.nava.mlsample.domain.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Basic integration tests for FreeMarker application.
 *
 * @author Niko Schmuck
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarkLogicSampleApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class SampleWebControllerIT {

    @Value("${local.server.port}")
    private int port;

    private RestTemplate template = new TestRestTemplate();

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void thatFreeMarkerIndexTemplateResponds() throws Exception {
        ResponseEntity<String> entity = template.getForEntity(getBaseUrl(), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertThat(entity.getBody(), containsString("Sample web application: MarkLogic with SpringBoot"));
    }

    @Test
    public void thatFreeMarkerErrorTemplateResponds() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = template.exchange(
                getBaseUrl() + "/does-not-exist", HttpMethod.GET,
                requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody(), containsString("Something went wrong: 404 Not Found"));
    }

    @Test
    public void thatProductsAsJSONResponds() {
        ResponseEntity<Product[]> entity = template.getForEntity(getBaseUrl() + "/products.json", Product[].class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(10, entity.getBody().length);
    }

}
