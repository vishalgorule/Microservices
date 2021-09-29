package com.microservices.demo.reactive.elastic.query.web.client.service;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryWebClient {

    Flux<ElasticQueryClientResponseModel> getDataByText(ElasticQueryClientRequestModel request);
}
