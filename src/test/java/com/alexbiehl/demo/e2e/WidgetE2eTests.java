package com.alexbiehl.demo.e2e;

import com.alexbiehl.demo.TestUtils;
import com.alexbiehl.demo.model.User;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.repository.UserRepository;
import com.alexbiehl.demo.repository.WidgetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetE2eTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WidgetRepository widgetRepository;
    @Autowired
    private UserRepository userRepository;
    @LocalServerPort
    private int port;


    @Test
    public void adminCreateWidget_userRead_andOk() {
        User admin = userRepository.findByUsername("admin");
        //String content = "{\"shortDescription\":\"test widget\",\"description\":\"test description\"}";
        Widget content = new Widget("test widget", "test description");

        HttpHeaders headers = TestUtils.headers(admin.getUsername());
        HttpEntity<Widget> entity = new HttpEntity<>(content, headers);

        ResponseEntity<Widget> response = this.restTemplate.postForEntity(
                TestUtils.uri(this.restTemplate, "/widgets"),
                entity,
                Widget.class
        );
        Widget createdWidget = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(createdWidget);

        User user = userRepository.findByUsername("user");

        RequestEntity<Widget> getEntity = new RequestEntity<>(
                TestUtils.headers(user.getUsername()),
                HttpMethod.GET,
                TestUtils.uri(this.restTemplate, "/widgets/" + createdWidget.getId())
        );
        ResponseEntity<Widget> getResponse = this.restTemplate.exchange(
                getEntity,
                Widget.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(createdWidget.getId(), getResponse.getBody().getId());
        assertEquals(createdWidget.getShortDescription(), getResponse.getBody().getShortDescription());
        assertEquals(createdWidget.getDescription(), getResponse.getBody().getDescription());
    }
}
