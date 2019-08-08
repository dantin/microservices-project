package com.github.dantin.microservices.composite.product.service;

import com.github.dantin.microservices.composite.product.model.ProductAggregated;
import com.github.dantin.microservices.core.product.model.Product;
import com.github.dantin.microservices.core.recommendation.model.Recommendation;
import com.github.dantin.microservices.core.review.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeService.class);

    private final ProductCompositeIntegration integration;

    private final Util util;

    public ProductCompositeService(ProductCompositeIntegration integration, Util util) {
        this.integration = integration;
        this.util = util;
    }

    @RequestMapping("/")
    public String index() {
        return "{\"timestamp\":\"" + new Date() + "\",\"content\":\"Hello from Product API\"}";
    }

    @RequestMapping("/product/{productId}")
    public ResponseEntity<ProductAggregated> getProduct(@PathVariable int productId) {

        // 1. Get mandatory product information.
        ResponseEntity<Product> productResponse = integration.getProduct(productId);
        if (!productResponse.getStatusCode().is2xxSuccessful()) {
            // can't proceed, return whatever fault we got from the get product call.
            return util.createResponse((ProductAggregated) null, productResponse.getStatusCode());
        }
        Product product = productResponse.getBody();
        if (product == null) {
            return util.createResponse((ProductAggregated) null, HttpStatus.NOT_FOUND);
        }

        // 2. Get optional recommendations.
        List<Recommendation> recommendations = null;
        try {
            ResponseEntity<List<Recommendation>> recommendationResponse = integration.getRecommendations(productId);
            if (!recommendationResponse.getStatusCode().is2xxSuccessful()) {
                // something went wrong with get recommendations, skip
                LOG.debug("call to get recommendation fail: {}", recommendationResponse.getStatusCode());
            } else {
                recommendations = recommendationResponse.getBody();
            }
        } catch (Throwable t) {
            LOG.error("get recommendation error", t);
            throw t;
        }

        // 3. Get optional reviews.
        ResponseEntity<List<Review>> reviewResponse = integration.getReviews(productId);
        List<Review> reviews = null;
        if (!reviewResponse.getStatusCode().is2xxSuccessful()) {
            // something went wrong with get review, skip
            LOG.debug("call to get review fail: {}", reviewResponse.getStatusCode());
        } else {
            reviews = reviewResponse.getBody();
        }

        return util.createOkResponse(new ProductAggregated(product, recommendations, reviews));
    }

}
