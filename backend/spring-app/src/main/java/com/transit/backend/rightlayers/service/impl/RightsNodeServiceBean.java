package com.transit.backend.rightlayers.service.impl;

import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import com.transit.backend.rightlayers.service.RightsNodeService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class RightsNodeServiceBean implements RightsNodeService {
	
	@Autowired
	private WebClient client;
	
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	
	@Override
	public String readEntityCLass(UUID primaryKey) {
		return this.client
				.get()
				.uri(uriBuilder ->
						uriBuilder.path("/helpers/entityclass/" + primaryKey)
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just("");
					} else if (response.statusCode().isError()) {
						return (Mono<String>) this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<String>() {
						});
					} else {
						return Mono.error(new RuntimeException(String.valueOf(response.statusCode()) + " from rights service."));
					}
				})
				.block();
	}
	
	
	@Override
	public Boolean nodeMultipleOutgoingEdges(UUID primaryKey) {
		return this.client
				.get()
				.uri(uriBuilder ->
						uriBuilder.path("/helpers/isshared/" + primaryKey + "")
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(Boolean.FALSE);
					} else if (response.statusCode().isError()) {
						return (Mono<Boolean>) this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<Boolean>() {
						});
					} else {
						return Mono.error(new RuntimeException(String.valueOf(response.statusCode()) + " from rights service."));
					}
				})
				.block();
	}
	
}