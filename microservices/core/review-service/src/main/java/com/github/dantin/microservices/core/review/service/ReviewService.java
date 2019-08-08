package com.github.dantin.microservices.core.review.service;

import com.github.dantin.microservices.core.review.model.Review;
import com.github.dantin.microservices.core.review.service.util.SetProcTimeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private final SetProcTimeBean setProcTimeBean;

    public ReviewService(SetProcTimeBean setProcTimeBean) {
        this.setProcTimeBean = setProcTimeBean;
    }

    @RequestMapping("/review")
    public List<Review> getReviews(
            @RequestParam(value = "productId", required = true) int productId) {

        int interval = setProcTimeBean.calculateProcessingTime();
        LOG.info("/review called, processing time: {} ms", interval);

        sleep(interval);

        List<Review> list = Arrays.asList(
                new Review(productId, 1, "Author 1", "Subject 1", "Content 1"),
                new Review(productId, 2, "Author 2", "Subject 2", "Content 2"),
                new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

        LOG.info("/review response size: {}", list.size());

        return list;
    }

    @RequestMapping("/set-processing-time")
    public void setProcessingTime(
            @RequestParam(value = "minMs", required = true) int minMs,
            @RequestParam(value = "maxMs", required = true) int maxMs) {

        LOG.info("/set-processing-time called: {} - {} ms", minMs, maxMs);

        setProcTimeBean.setDefaultProcessingTime(minMs, maxMs);
    }

    private void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
