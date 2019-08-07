package com.github.dantin.microservices.core.review.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SetProcTimeBean {

    private static final Logger LOG = LoggerFactory.getLogger(SetProcTimeBean.class);

    @Value("${service.defaultMinMs}")
    private int minMs;

    @Value(value = "${service.defaultMaxMs}")
    private int maxMs;

    /**
     * Set processing time.
     *
     * @param minMs minimum time in milliseconds
     * @param maxMs maximum time in milliseconds
     */
    public void setDefaultProcessingTime(int minMs, int maxMs) {
        this.minMs = Math.max(minMs, 0);
        maxMs = Math.max(minMs, maxMs);
        this.maxMs = Math.max(maxMs, 0);

        LOG.info("set response time to {} - {} ms", this.minMs, this.maxMs);
    }

    /**
     * Calculate processing time in milliseconds.
     *
     * @return random time between [minMs, maxMs].
     */
    public int calculateProcessingTime() {
        int processingTimeMs = minMs + (int) (Math.random() * (maxMs - minMs));
        LOG.debug("return calculated processing time: {} ms", processingTimeMs);
        return processingTimeMs;
    }

}
