package com.flightapp.flights;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test_db",
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
class FlightsServiceApplicationTests {

    @Test
    void contextLoads() {
        // Application context loads successfully
    }
}
