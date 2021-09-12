package com.microservices.demo.kafka.to.elastic.service.transformer;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToElasticModelTransformer {

    public List<TwitterIndexModel> getElasticModel (List<TwitterAvroModel> documents){
        return documents.stream()
                .map(doc -> TwitterIndexModel
                        .builder()
                        .userId(doc.getUserId())
                        .id(String.valueOf(doc.getId()))
                        .text(doc.getText())
                        .createdAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(doc.getCreatedAt()),
                                ZoneId.systemDefault()))
                        .build()
                ).collect(Collectors.toList());
    }

}
