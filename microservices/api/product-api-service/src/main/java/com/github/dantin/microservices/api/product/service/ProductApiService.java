package com.github.dantin.microservices.api.product.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;

@RestController
public class ProductApiService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductApiService.class);

    private final LoadBalancerClient loadBalancer;

    private RestTemplate restTemplate = new RestTemplate();

    public ProductApiService(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @RequestMapping("/{productId}")
    @HystrixCommand(fallbackMethod = "defaultProductComposite")
    public ResponseEntity<String> getProductComposite(
            @PathVariable int productId,
            @RequestHeader(value = "Authorization") String authorizationHeader,
            Principal currentUser) {

        LOG.info("product-api: user={}, auth={}, called with productId={}", currentUser.getName(), authorizationHeader, productId);
        URI uri = loadBalancer.choose("productcomposite").getUri();
        String url = uri.toString() + "/product/" + productId;

        LOG.debug("get product composite from URL: '{}'", url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        LOG.info("get product composite http-status: {}", response.getStatusCode());
        LOG.debug("get product composite body: {}", response.getBody());

        return response;
    }

    public ResponseEntity<String> defaultProductComposite(
            @PathVariable int productId,
            @RequestHeader(value = "Authorization") String authorizationHeader,
            Principal currentUser) {

        LOG.warn("use fallback method for product-composite-service, user={}, auth={}, called with productId={}", currentUser.getName(), authorizationHeader, productId);
        return new ResponseEntity<>("", HttpStatus.BAD_GATEWAY);
    }

}
