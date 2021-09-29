package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.impl.TwitterElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final TwitterElasticQueryService twitterElasticQueryService;

    @Value("${server.port}")
    private String port;

    public ElasticDocumentController(TwitterElasticQueryService twitterElasticQueryService) {
        this.twitterElasticQueryService = twitterElasticQueryService;
    }

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @Operation(summary = "Get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = twitterElasticQueryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @PostAuthorize("hasPermission(#id, 'ElasticQueryServiceResponseModel', 'READ')")
    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel response = twitterElasticQueryService.getDocumentById(id);
        LOG.info("Elasticsearch returned document with id "+ id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel response = twitterElasticQueryService.getDocumentById(id);
        ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(response);
        LOG.info("Elasticsearch returned document with id "+ id);
        return ResponseEntity.ok(responseModelV2);
    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @Operation(summary = "Get elastic document by text.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/get-document-by-text")
    public ResponseEntity<ElasticQueryServiceAnalyticsResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel request,
            @AuthenticationPrincipal TwitterQueryUser principal,
            @RegisteredOAuth2AuthorizedClient("keycloak")OAuth2AuthorizedClient oAuth2AuthorizedClient){
        LOG.info("User {} querying documents for text {}", principal.getUsername(),
                request.getText());
        ElasticQueryServiceAnalyticsResponseModel response =
                twitterElasticQueryService.getDocumentsByText(request.getText(),
                        oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        LOG.info("Elasticsearch returned {} number of documents with text {} on port {}",
                response.getQueryResponseModels().size(), request.getText(), port);
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel v1Model){
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(v1Model.getId()))
                .text(v1Model.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(v1Model.getLinks());
        return responseModelV2;
    }
}
