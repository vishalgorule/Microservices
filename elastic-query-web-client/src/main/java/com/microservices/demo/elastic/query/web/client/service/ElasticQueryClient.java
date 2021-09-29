package com.microservices.demo.elastic.query.web.client.service;


import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;

import java.util.List;

public interface ElasticQueryClient {

    ElasticQueryWebClientAnalyticsResponseModel getDataByText (ElasticQueryClientRequestModel requestModel);

}
