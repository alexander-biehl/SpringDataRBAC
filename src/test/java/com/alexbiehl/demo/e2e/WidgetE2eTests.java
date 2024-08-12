package com.alexbiehl.demo.e2e;

import com.alexbiehl.demo.TestConstants;
import com.alexbiehl.demo.TestUtils;
import com.alexbiehl.demo.model.User;
import com.alexbiehl.demo.model.Widget;
import com.alexbiehl.demo.repository.UserRepository;
import com.alexbiehl.demo.repository.WidgetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetE2eTests {

    private static final Logger log = LoggerFactory.getLogger(WidgetE2eTests.class);

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

    @Autowired
    private AclCache aclCache;

    @AfterEach
    public void tearDown() {
        aclCache.clearCache();
    }


    @Test
    public void adminCreateWidget_userRead_andOk() {
        User admin = userRepository.findByUsername("admin");
        Widget content = new Widget("test widget", "test description");

        HttpHeaders headers = TestUtils.headers(admin.getUsername());
        HttpEntity<Widget> entity = new HttpEntity<>(content, headers);

        ResponseEntity<Widget> response = this.restTemplate.postForEntity(
                TestUtils.uri(this.restTemplate, "/widgets"),
                entity,
                Widget.class
        );
        Widget createdWidget = response.getBody();
        String newLoc = response.getHeaders().getLocation().getPath();
        String resourceId = newLoc.substring(newLoc.length() - 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(createdWidget);

        User user = userRepository.findByUsername("user");

        RequestEntity<Widget> getEntity = new RequestEntity<>(
                TestUtils.headers(user.getUsername()),
                HttpMethod.GET,
                TestUtils.uri(this.restTemplate, "/widgets/" + resourceId)
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

    @Test
    public void givenWidget_userUpdate_andFail() throws Exception{

        Tuple<Widget, User> result = runWithSecurity(new DBFunction());
        Widget testWidget = result.getKey();
        User user = result.getValue();

        testWidget.setDescription("updated description");

        ResponseEntity<String> response = this.restTemplate.exchange(
                RequestEntity.put(
                                TestUtils.uri(this.restTemplate, "/widgets/" + testWidget.getId())
                        )
                        .headers(TestUtils.headers(user.getUsername()))
                        .body(testWidget),
                String.class
        );
        log.info("response: {}", response.toString());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void givenWidget_userDelete_andFail() throws Exception {
        Tuple<Widget, User> result = runWithSecurity(new DBFunction());
        Widget testWidget = result.getKey();
        User user = result.getValue();

        ResponseEntity<String> response = this.restTemplate.exchange(
                RequestEntity.delete(TestUtils.uri(this.restTemplate, "/widgets/" + testWidget.getId()))
                        .headers(TestUtils.headers(user.getUsername()))
                        .build(),
                String.class
        );
        log.info("response: {}", response.toString());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private class DBFunction implements Function<Tuple<Widget,User>> {
        @Override
        public Tuple<Widget, User> call() {
            Widget testWidget = widgetRepository.findById(TestConstants.TEST_WIDGET_ID);
            User user = userRepository.findByUsername("user");
            return new Tuple<>(testWidget, user);
        }
    }

    private interface Function<V> {
        V call();
    }

    private <K,V> Tuple<K, V> runWithSecurity(Function<Tuple<K,V>> function) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        "password",
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
        Tuple<K, V> result = function.call();
        SecurityContextHolder.getContext();
        return result;
    }

    private class Tuple<K, V> extends AbstractMap.SimpleEntry<K, V> {

        public Tuple(K key, V value) {
            super(key, value);
        }

        public Tuple(Map.Entry entry) {
            super(entry);
        }
    }
}
