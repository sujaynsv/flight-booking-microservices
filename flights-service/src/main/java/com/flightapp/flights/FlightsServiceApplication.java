package com.flightapp.flights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FlightsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightsServiceApplication.class, args);
    }
}
