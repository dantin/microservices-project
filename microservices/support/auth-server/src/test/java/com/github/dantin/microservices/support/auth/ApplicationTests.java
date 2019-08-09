package com.github.dantin.microservices.support.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void homePageProtected() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/uaa/", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        String auth = response.getHeaders().getFirst("WWW-Authenticate");
        assertTrue("Wrong header: " + auth, auth.startsWith("Bearer realm=\""));
    }

    @Test
    public void userEndpointProtected() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/uaa/endpoint", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        String auth = response.getHeaders().getFirst("WWW-Authenticate");
        assertTrue("Wrong header: " + auth, auth.startsWith("Bearer realm=\""));
    }

    @Test
    public void authorizationRedirects() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/uaa/oauth/authorize", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        String auth = response.getHeaders().getFirst("WWW-Authenticate");
        assertTrue("Wrong header: " + auth, auth.startsWith("Bearer realm=\""));
    }

}
