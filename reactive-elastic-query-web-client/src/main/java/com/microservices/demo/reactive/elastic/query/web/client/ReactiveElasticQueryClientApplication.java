package com.microservices.demo.reactive.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
public class ReactiveElasticQueryClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveElasticQueryClientApplication.class, args);
    }

}
