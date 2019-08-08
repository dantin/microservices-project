package com.github.dantin.microservices.composite.product.model;

import com.github.dantin.microservices.core.product.model.Product;
import com.github.dantin.microservices.core.recommendation.model.Recommendation;
import com.github.dantin.microservices.core.review.model.Review;

import java.util.List;
import java.util.stream.Collectors;

public class ProductAggregated {

    private int productId;
    private String name;
    private int weight;
    private List<RecommendationSummary> recommendations;
    private List<ReviewSummary> reviews;

    public ProductAggregated(Product product, List<Recommendation> recommendations, List<Review> reviews) {
        // setup product information.
        this.productId = product.getProductId();
        this.name = product.getName();
        this.weight = product.getWeight();

        // copy summary recommendation information, if any.
        if (recommendations != null) {
            this.recommendations = recommendations.stream()
                    .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
                    .collect(Collectors.toList());
        }

        if (reviews != null) {
            this.reviews = reviews.stream()
                    .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                    .collect(Collectors.toList());
        }
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<RecommendationSummary> getRecommendations() {
        return recommendations;
    }

    public List<ReviewSummary> getReviews() {
        return reviews;
    }
}
