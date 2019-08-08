package com.github.dantin.microservices.composite.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dantin.microservices.core.product.model.Product;
import com.github.dantin.microservices.core.recommendation.model.Recommendation;
import com.github.dantin.microservices.core.review.model.Review;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
public class ProductCompositeIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final Util util;

    private RestTemplate restTemplate = new RestTemplate();

    public ProductCompositeIntegration(Util util) {
        this.util = util;
    }

    /**
     * Get product by id.
     *
     * @param productId product id
     * @return product entity
     */
    @HystrixCommand(fallbackMethod = "defaultProduct")
    public ResponseEntity<Product> getProduct(int productId) {

        URI uri = util.getServiceUrl("product", "http://localhost:8081/product");
        String url = uri.toString() + "/product/" + productId;
        LOG.debug("get product from URL: {}", url);

        ResponseEntity<String> responseStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("get product http-status: {}", responseStr.getStatusCode());
        LOG.debug("get product body: {}", responseStr.getBody());

        Product product = response2Product(responseStr);

        return util.createOkResponse(product);
    }

    public ResponseEntity<Product> defaultProduct(int id) {
        LOG.warn("using fallback method for product-service");
        return util.createResponse(null, HttpStatus.BAD_GATEWAY);
    }

    private ObjectReader productReader = null;

    private ObjectReader getProductReader() {
        if (productReader != null) return productReader;

        ObjectMapper mapper = new ObjectMapper();
        productReader = mapper.readerFor(Product.class);
        return productReader;
    }

    private Product response2Product(ResponseEntity<String> response) {
        try {
            return getProductReader().readValue(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get recommendations from a product.
     *
     * @param productId product id
     * @return recommendation list
     */
    @HystrixCommand(fallbackMethod = "defaultRecommendation")
    public ResponseEntity<List<Recommendation>> getRecommendations(int productId) {
        try {
            LOG.info("get recommendations...");

            URI uri = util.getServiceUrl("recommendation", "http://localhost:8081/recommendation");
            String url = uri.toString() + "/recommendation?productId=" + productId;
            LOG.debug("get recommendations from URL: {}", url);

            ResponseEntity<String> responseStr = restTemplate.getForEntity(url, String.class);
            LOG.debug("get recommendations http-status: {}", responseStr.getStatusCode());
            LOG.debug("get recommendations body: {}", responseStr.getBody());

            List<Recommendation> recommendations = response2Recommendations(responseStr);
            LOG.debug("get recommendations count: {}", recommendations.size());

            return util.createOkResponse(recommendations);
        } catch (Throwable t) {
            LOG.error("get recommendations error", t);
            throw t;
        }
    }

    public ResponseEntity<List<Recommendation>> defaultRecommendation(int id) {
        LOG.warn("using fallback method for recommendation-service");
        return util.createResponse(null, HttpStatus.BAD_GATEWAY);
    }

    private List<Recommendation> response2Recommendations(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List list = mapper.readValue(response.getBody(), new TypeReference<List<Recommendation>>() {
            });
            List<Recommendation> recommendations = list;
            return recommendations;
        } catch (IOException e) {
            LOG.warn("fail to read recommendation JSON", e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            LOG.warn("fail to parse recommendations JSON", e);
            throw e;
        }
    }

    /**
     * Get review list from a product.
     *
     * @param productId product id
     * @return review list
     */
    @HystrixCommand(fallbackMethod = "defaultReview")
    public ResponseEntity<List<Review>> getReviews(int productId) {
        LOG.info("get reviews...");

        URI uri = util.getServiceUrl("review", "http://localhost:8081/review");
        String url = uri.toString() + "/review?productId=" + productId;
        LOG.debug("get reviews from URL: {}", url);

        ResponseEntity<String> responseStr = restTemplate.getForEntity(url, String.class);
        LOG.debug("get reviews http-status: {}", responseStr.getStatusCode());
        LOG.debug("get reviews body: {}", responseStr.getBody());

        List<Review> reviews = response2Reviews(responseStr);
        LOG.debug("get reviews count: {}", reviews.size());

        return util.createOkResponse(reviews);
    }

    public ResponseEntity<List<Review>> defaultReview(int id) {
        LOG.warn("using fallback method for review-service");
        return util.createResponse(null, HttpStatus.BAD_GATEWAY);
    }

    private List<Review> response2Reviews(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List list = mapper.readValue(response.getBody(), new TypeReference<List<Review>>() {
            });
            List<Review> reviews = list;
            return reviews;
        } catch (IOException e) {
            LOG.warn("fail to read reviews JSON", e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            LOG.warn("fail to parse reviews JSON", e);
            throw e;
        }
    }

}
