package com.microservices.demo.elastic.query.web.client.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryClientResponseModel {

    private String id;
    private Long userId;
    private String text;
    private ZonedDateTime createdAt;

}
