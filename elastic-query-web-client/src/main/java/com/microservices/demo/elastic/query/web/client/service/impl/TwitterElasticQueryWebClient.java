package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryClientConfigData;
import com.microservices.demo.mdc.Constants;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient.Builder webClientBuilder;

    private final ElasticQueryClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder clientBuilder,
                                        ElasticQueryClientConfigData webClientConfigData) {
        this.webClientBuilder = clientBuilder;
        this.elasticQueryWebClientConfigData = webClientConfigData;
    }

    @Override
    public ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToMono(ElasticQueryWebClientAnalyticsResponseModel.class)
                .log()//Logger
                .block();
    }

    @Override
    public List<ElasticQueryClientResponseModel> getDataByText2(ElasticQueryClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryClientResponseModel.class)
                .collectList()
                .block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryClientRequestModel requestModel) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
                .header(Constants.CORRELATION_ID_HEADER, MDC.get(Constants.CORRELATION_ID_KEY))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(
                                new ElasticQueryClientException(clientResponse.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase())));
    }


    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }
}
