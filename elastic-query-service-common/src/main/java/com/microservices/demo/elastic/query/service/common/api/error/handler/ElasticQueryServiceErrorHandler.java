package com.microservices.demo.elastic.query.service.common.api.error.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ElasticQueryServiceErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticQueryServiceErrorHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handle(AccessDeniedException e){
        LOG.info("Access denied exception", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorised to access this resource");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException e){
        LOG.info("Illegal Argument Exception", e);
        return ResponseEntity.badRequest().body("Illegal argument exception " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e){
        LOG.info("Service Runtime Exception", e);
        return ResponseEntity.badRequest().body("Server runtime exception " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e){
        LOG.info("Internal server error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A server error occurred");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e) {
        LOG.info("Method argument validation exception", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
