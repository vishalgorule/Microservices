package com.microservices.demo.elastic.query.web.client.common.exception;

public class ElasticQueryClientException extends RuntimeException{

    public ElasticQueryClientException() {
        super();
    }

    public ElasticQueryClientException(String message) {
        super(message);
    }

    public ElasticQueryClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
