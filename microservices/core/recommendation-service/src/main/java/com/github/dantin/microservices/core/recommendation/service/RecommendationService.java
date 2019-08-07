package com.github.dantin.microservices.core.recommendation.service;

import com.github.dantin.microservices.core.recommendation.model.Recommendation;
import com.github.dantin.microservices.core.recommendation.service.util.SetProcTimeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

    private final SetProcTimeBean setProcTimeBean;

    public RecommendationService(SetProcTimeBean setProcTimeBean) {
        this.setProcTimeBean = setProcTimeBean;
    }

    @RequestMapping("/recommendation")
    public List<Recommendation> getRecommendations(
            @RequestParam(value = "productId", required = true) int productId) {

        int interval = setProcTimeBean.calculateProcessingTime();
        LOG.info("/recommendation called, processing time: {} ms", interval);

        sleep(interval);

        List<Recommendation> list = Arrays.asList(
                new Recommendation(productId, 1, "Author 1", 1, "Content 1"),
                new Recommendation(productId, 2, "Author 2", 2, "Content 2"),
                new Recommendation(productId, 3, "Author 3", 3, "Content 3"));

        LOG.info("/recommendation response size: {}", list.size());

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
