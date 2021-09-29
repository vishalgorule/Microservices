package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionsUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticsearchQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final TwitterElasticsearchQueryRepository elasticsearchQueryRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticsearchQueryRepository repository) {
        this.elasticsearchQueryRepository = repository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = elasticsearchQueryRepository.findById(id);
        LOG.info("Document with id {} retrieved successfully",
                searchResult.orElseThrow(() ->
                        new ElasticQueryClientException("No document found at elasticsearch with id "+ id)));
        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = elasticsearchQueryRepository.findByText(text);
        LOG.info("{} of documents with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModel() {
        Iterable<TwitterIndexModel> searchResult = elasticsearchQueryRepository.findAll();
        List<TwitterIndexModel> list = CollectionsUtil.getInstance().getListFromIterable(searchResult);
        LOG.info("{} number of documents retrieved successfully", list.size());
        return list;
    }
}
