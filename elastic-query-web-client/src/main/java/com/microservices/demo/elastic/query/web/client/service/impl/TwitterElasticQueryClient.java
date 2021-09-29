package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryClient implements ElasticQueryClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final WebClient.Builder webClientBuilder;
    private final ElasticQueryClientConfigData elasticQueryClientConfigData;


    public TwitterElasticQueryClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder,
                                     ElasticQueryClientConfigData elasticQueryClientConfigData) {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryClientConfigData = elasticQueryClientConfigData;
    }


    @Override
    public ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToMono(ElasticQueryWebClientAnalyticsResponseModel.class)
                .block();
    }

    private WebClient.ResponseSpec getWebClient (ElasticQueryClientRequestModel request){
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryClientConfigData.getQueryByText().getAccept()))
                .body(BodyInserters.fromPublisher(Mono.just(request), createParameterized()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        cr -> Mono.just(new ElasticQueryClientException(cr.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())));

    }

    private <T> ParameterizedTypeReference<T> createParameterized() {
        return new ParameterizedTypeReference<>() {
        };
    }

}
