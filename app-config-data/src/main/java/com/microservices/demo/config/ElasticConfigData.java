package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "elastic-config")
public class ElasticConfigData {

    private String indexName;
    private String connectionUrl;
    private Integer connectionTimeoutMs;
    private Integer socketTimeoutMs;

}
