package com.example.testdrivesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TestDrivesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestDrivesServiceApplication.class, args);
    }

}