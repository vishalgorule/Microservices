package com.microservices.demo.elastic.query.web.client.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticQueryWebClientAnalyticsResponseModel {

    private List<ElasticQueryClientResponseModel> queryClientResponseModelList;
    private String word;

}
