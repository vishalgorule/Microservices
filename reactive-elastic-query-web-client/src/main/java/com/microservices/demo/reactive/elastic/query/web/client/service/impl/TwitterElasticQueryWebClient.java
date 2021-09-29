package com.microservices.demo.reactive.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import com.microservices.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient webClient;

    private final ElasticQueryClientConfigData elasticQueryWebClientConfig;

    public TwitterElasticQueryWebClient(@Qualifier("webClient") WebClient client,
                                        ElasticQueryClientConfigData configData) {
        this.webClient = client;
        this.elasticQueryWebClientConfig = configData;
    }


    @Override
    public Flux<ElasticQueryClientResponseModel> getDataByText(ElasticQueryClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryClientResponseModel.class);
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryClientRequestModel requestModel) {
        return webClient
                .method(HttpMethod.valueOf(elasticQueryWebClientConfig.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfig.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfig.getQueryByText().getAccept()))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        cr -> Mono.just(new ElasticQueryClientException(cr.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())));
    }


    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }
}
