package com.transit.backend.rightlayers.service.impl;

import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import com.transit.backend.rightlayers.domain.DeleteObjectDTO;
import com.transit.backend.rightlayers.domain.ObjectDTO;
import com.transit.backend.rightlayers.domain.ObjectResponseDTO;
import com.transit.backend.rightlayers.service.ObjectService;
import org.apache.commons.codec.BinaryDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class ObjectServiceBean implements ObjectService {
	
	@Autowired
	private WebClient client;
	
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	@Override
	public ObjectResponseDTO createObject(UUID objectId, String objectEntityClass, UUID identityId, Set<String> properties) {
		ObjectDTO dto = new ObjectDTO();
		dto.setIdentityId(identityId);
		dto.setObjectId(objectId);
		dto.setObjectEntityClass(objectEntityClass);
		dto.setProperties(properties);
		return (ObjectResponseDTO) this.client
				.post()
				.uri(uriBuilder ->
						uriBuilder.path("/object/")
								.build())
				.bodyValue(dto)
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.CREATED)) {
						return response.bodyToMono(new ParameterizedTypeReference<ObjectResponseDTO>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public Optional<ObjectResponseDTO> updateObject(UUID objectId, String objectEntityClass, UUID identityId, Set<String> properties) {
		ObjectDTO dto = new ObjectDTO();
		dto.setIdentityId(identityId);
		dto.setObjectId(objectId);
		dto.setObjectEntityClass(objectEntityClass);
		dto.setProperties(properties);
		return (Optional<ObjectResponseDTO>) this.client
				.put()
				.uri(uriBuilder ->
						uriBuilder.path("/object/")
								.path(objectId.toString())
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(Optional.empty());
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<Optional<ObjectResponseDTO>>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public Optional<ObjectResponseDTO> getObject(UUID objectId) {
		
		return (Optional<ObjectResponseDTO>) this.client
				.get()
				.uri(uriBuilder ->
						uriBuilder.path("/object/")
								.path(objectId.toString())
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(Optional.empty());
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<Optional<ObjectResponseDTO>>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public boolean deleteObject(UUID identityId, UUID objectId) {
		
		
		return (boolean) this.client
				.delete()
				.uri(uriBuilder ->
						uriBuilder.path("/object/")
								.path(objectId.toString())
								.queryParam("requestedById", identityId)
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
