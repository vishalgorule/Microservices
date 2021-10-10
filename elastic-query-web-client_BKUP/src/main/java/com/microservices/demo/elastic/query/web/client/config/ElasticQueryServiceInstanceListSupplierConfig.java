package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.ElasticQueryClientConfigData;
import com.microservices.demo.elastic.query.web.client.api.QueryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Primary
public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    private final ElasticQueryClientConfigData.WebClient webClientConfig;

    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryClientConfigData webClientConfigData) {
        this.webClientConfig = webClientConfigData.getWebClient();
    }

    @Override
    public String getServiceId() {
        return webClientConfig.getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        LOG.info("Instances 1 {}", webClientConfig.getInstances().toArray());
        return Flux.just(
                webClientConfig.getInstances().stream()
                        .map(instance ->
                                new DefaultServiceInstance(
                                        instance.getId(),
                                        getServiceId(),
                                        instance.getHost(),
                                        instance.getPort(),
                                        false
                                )).collect(Collectors.toList()));
    }
}
