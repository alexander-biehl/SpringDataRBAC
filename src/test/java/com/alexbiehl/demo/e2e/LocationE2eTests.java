package com.alexbiehl.demo.e2e;

import com.alexbiehl.demo.TestUtils;
import com.alexbiehl.demo.model.Location;
import com.alexbiehl.demo.model.User;
import com.alexbiehl.demo.repository.LocationRepository;
import com.alexbiehl.demo.repository.UserRepository;
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
public class LocationE2eTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private LocationRepository locaRepository;

    @Test
    public void adminCreateLoc_userRead_andOk() {
        User admin = userRepository.findByUsername("admin");
        Location content = new Location(
                "test location",
                "test description",
                "321 Some Street",
                "Sometown",
                "AZ",
                "00001",
                "USA"
        );

        HttpEntity<Location> entity = new HttpEntity<>(
                content,
                TestUtils.headers(admin.getUsername())
        );

        ResponseEntity<Location> response = this.restTemplate.postForEntity(
                TestUtils.uri(this.restTemplate, "/locations"),
                entity,
                Location.class
        );

        Location createdLoc = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(createdLoc);

        User user = userRepository.findByUsername("user");

        RequestEntity<Location> getEntity = new RequestEntity<>(
                TestUtils.headers(user.getUsername()),
                HttpMethod.GET,
                TestUtils.uri(this.restTemplate, "/locations/" + createdLoc.getId())
        );
        ResponseEntity<Location> getResponse = this.restTemplate.exchange(
                getEntity,
                Location.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(createdLoc.getId(), getResponse.getBody().getId());
        assertEquals(createdLoc.getName(), getResponse.getBody().getName());
        assertEquals(createdLoc.getDescription(), getResponse.getBody().getDescription());
    }
}
