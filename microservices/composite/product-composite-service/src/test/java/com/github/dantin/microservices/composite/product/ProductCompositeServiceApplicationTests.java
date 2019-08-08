package com.github.dantin.microservices.composite.product;

import com.github.dantin.microservices.composite.product.service.ProductCompositeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"eureka.client.enabled:false"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCompositeServiceApplicationTests {

    @Autowired
    private ProductCompositeService service;

    @Test
    public void contextLoads() throws Exception {
        assertThat(service).isNotNull();
    }

}
