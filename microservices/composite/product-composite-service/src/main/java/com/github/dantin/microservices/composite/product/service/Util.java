package com.github.dantin.microservices.composite.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private final LoadBalancerClient loadBalancer;

    public Util(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * Retrieve service url using load balance.
     *
     * @param serviceId   service id
     * @param fallbackUri fallback uri
     * @return the target service uri or fallback uri
     */
    public URI getServiceUrl(String serviceId, String fallbackUri) {
        URI uri;
        try {
            ServiceInstance instance = loadBalancer.choose(serviceId);
            uri = instance.getUri();
            LOG.debug("resolved serviceId '{}' to URL '{}'", serviceId, uri);
        } catch (RuntimeException e) {
            // Eureka not available, use fallback.
            uri = URI.create(fallbackUri);
            LOG.warn("fail to resolve serviceId '{}', fallback to URL '{}'", serviceId, uri);
        }
        return uri;
    }

    /**
     * Create success HTTP response.
     *
     * @param body HTTP body
     * @param <T>  type
     * @return success HTTP response
     */
    public <T> ResponseEntity<T> createOkResponse(T body) {
        return createResponse(body, HttpStatus.OK);
    }

    /**
     * Create customized HTTP response.
     *
     * @param body       HTTP body
     * @param httpStatus HTTP status code
     * @param <T>        type
     * @return customized HTTP response
     */
    public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
        return new ResponseEntity<>(body, httpStatus);
    }
}
