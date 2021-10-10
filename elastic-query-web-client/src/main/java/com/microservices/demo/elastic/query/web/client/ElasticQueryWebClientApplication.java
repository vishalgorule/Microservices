package com.microservices.demo.elastic.query.web.client;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class ElasticQueryWebClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryWebClientApplication.class, args);
    }
}
