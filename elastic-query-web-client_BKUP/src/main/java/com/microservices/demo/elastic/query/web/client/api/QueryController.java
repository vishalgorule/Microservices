package com.microservices.demo.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryClientResponseModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class QueryController {

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    private final ElasticQueryClient elasticQueryClient;

    public QueryController(ElasticQueryClient elasticQueryClient) {
        this.elasticQueryClient = elasticQueryClient;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryClientRequestModel requestModel,
                              Model model) {
        LOG.info("Querying with text {}", requestModel.getText());
        ElasticQueryWebClientAnalyticsResponseModel responseModels = elasticQueryClient.getDataByText(requestModel);

        LOG.info("Result with text {}", responseModels.getQueryClientResponseModelList());
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels.getQueryClientResponseModelList());
        model.addAttribute("wordCount", responseModels.getWord());
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryClientRequestModel.builder().build());
        return "home";
    }

}
