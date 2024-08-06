package com.alexbiehl.demo;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Arrays;

public class TestUtils {

    public static URI uri(@NonNull TestRestTemplate restTemplate, @NonNull String path) {
        return restTemplate.getRestTemplate().getUriTemplateHandler().expand(path);
    }

    public static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public static HttpHeaders headers(String username) {
        HttpHeaders headers = headers();
        headers.setBasicAuth(username, "password");
        return headers;
    }
}
