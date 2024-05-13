package com.transit.backend.rightlayers.service.impl;

import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import com.transit.backend.rightlayers.domain.IdentityDTO;
import com.transit.backend.rightlayers.domain.UserAttributeClazz;
import com.transit.backend.rightlayers.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class IdentityServiceBean implements IdentityService {
	
	@Autowired
	private WebClient client;
	
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	
	@Override
	public UserAttributeClazz createIdentity(UUID identityId) {
		IdentityDTO dto = new IdentityDTO();
		dto.setId(identityId);
		return (UserAttributeClazz) this.client
				.post()
				.uri(uriBuilder ->
						uriBuilder.path("/identity/")
								.build())
				.bodyValue(dto)
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.CREATED)) {
						return response.bodyToMono(new ParameterizedTypeReference<UserAttributeClazz>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public Optional<UserAttributeClazz> getIdentity(UUID identityId) {
		
		return (Optional<UserAttributeClazz>) this.client
				.get()
				.uri(uriBuilder ->
						uriBuilder.path("/identity/")
								.path(identityId.toString())
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(Optional.empty());
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<Optional<UserAttributeClazz>>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public boolean deleteIdentity(UUID id) {
		
		return (boolean) this.client
				.delete()
				.uri(uriBuilder ->
						uriBuilder.path("/object/")
								.path(id.toString())
								.build())
				
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NO_CONTENT)) {
						return Mono.just(true);
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else {
						return Mono.just(false);
					}
				})
				.block();
	}
}
