package com.transit.backend.rightlayers.service.impl;

import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import com.transit.backend.rightlayers.domain.AccessListDTO;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.domain.AccessResponseList;
import com.transit.backend.rightlayers.domain.OAPropertiesDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class AccessServiceBean implements AccessService {
	
	@Autowired
	private WebClient client;
	
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	
	@Override
	public boolean updateConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId) {
		return this.updateConnection(readProperties, writeProperties, oId, identityId, userHelperFunctions.getCompanyId());
	}
	
	@Override
	public boolean updateConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId, UUID requestedById) {
		
		OAPropertiesDTO dto = new OAPropertiesDTO();
		dto.setReadProperties(readProperties);
		dto.setWriteProperties(writeProperties);
		
		return (boolean) this.client
				.put()
				.uri(uriBuilder ->
						uriBuilder.path("/access/")
								.path(oId.toString())
								.queryParam("identityId", identityId)
								.queryParam("requestedById", requestedById)
								.build())
				.bodyValue(dto)
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(false);
						
					} else if (response.statusCode().equals(HttpStatus.NO_CONTENT)) {
						return Mono.just(true);
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					}
					return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					
				})
				.block();
		
	}
	
	@Override
	public Optional<AccessResponseDTO> getAccess(UUID oId) {
		
		var compId = userHelperFunctions.getCompanyId();
		return this.getAccess(oId, compId, compId);
	}
	
	@Override
	public Optional<AccessResponseDTO> getAccess(UUID oId, UUID identityId) {
		return this.getAccess(oId, identityId, userHelperFunctions.getCompanyId());
	}
	
	@Override
	public Optional<AccessResponseDTO> getAccess(UUID oId, UUID identityId, UUID requestedById) {
		return (Optional<AccessResponseDTO>) this.client
				.get()
				.uri(uriBuilder ->
						uriBuilder.path("/access/")
								.path(oId.toString())
								.queryParam("identityId", identityId)
								.queryParam("requestedById", requestedById)
								.build())
				
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(Optional.empty());
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<Optional<AccessResponseDTO>>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	@Override
	public StorageRights getAccessList(Set<UUID> objectIds) {
		var compId = userHelperFunctions.getCompanyId();
		return this.getAccessList(objectIds, compId, compId);
	}
	
	@Override
	public StorageRights getAccessList(Set<UUID> objectIds, UUID identityId, UUID requestedById) {
		AccessListDTO dto = new AccessListDTO();
		dto.setObjectIds(objectIds);
		var temp = (AccessResponseList) this.client
				.method(HttpMethod.GET)
				.uri(uriBuilder ->
						uriBuilder.path("/access/")
								.queryParam("identityId", identityId)
								.queryParam("requestedById", requestedById)
								.build())
				.bodyValue(dto)
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<AccessResponseList>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
		StorageRights rights = new StorageRights();
		var reMap = new HashMap<UUID, Optional<AccessResponseDTO>>();
		objectIds.forEach(id -> {
			if (temp == null) {
				reMap.put(id, Optional.empty());
			}
			var findX = temp.getObjects().stream().filter(entry -> entry.getObjectId().equals(id)).findFirst();
			if (findX.isPresent()) {
				reMap.put(id, findX);
			} else {
				reMap.put(id, Optional.empty());
			}
		});
		rights.setRightsForResponse(reMap);
		return rights;
	}
	
	@Override
	public AccessResponseList getAccessClazz(String entityClazz, boolean createdByMyCompany) {
		var compID = userHelperFunctions.getCompanyId();
		return this.getAccessClazz(entityClazz, compID, createdByMyCompany);
	}
	
	@Override
	public AccessResponseList getAccessClazz(String entityClazz, boolean createdByMyCompany, UUID compID) {
		
		return this.getAccessClazz(entityClazz, compID, createdByMyCompany);
	}
	
	@Override
	public AccessResponseList getAccessClazz(String entityClazz, UUID requestedById, boolean createdByMyCompany) {
		return (AccessResponseList) this.client
				.method(HttpMethod.GET)
				.uri(uriBuilder ->
						uriBuilder.path("/access/search")
								
								.queryParam("requestedById", requestedById)
								.queryParam("objectEntityClass", entityClazz)
								.queryParam("createdByMyOwn", createdByMyCompany)
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					} else if (response.statusCode().equals(HttpStatus.OK)) {
						return response.bodyToMono(new ParameterizedTypeReference<AccessResponseList>() {
						});
					} else {
						return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					}
				})
				.block();
	}
	
	
	@Override
	public boolean createConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId) {
		return this.createConnection(readProperties, writeProperties, oId, identityId, userHelperFunctions.getCompanyId());
	}
	
	@Override
	public boolean createConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId, UUID requestedById) {
		OAPropertiesDTO dto = new OAPropertiesDTO();
		dto.setReadProperties(readProperties);
		dto.setWriteProperties(writeProperties);
		return (boolean) this.client
				.post()
				.uri(uriBuilder ->
						uriBuilder.path("/access/")
								.path(oId.toString())
								.queryParam("identityId", identityId)
								.queryParam("requestedById", requestedById)
								.build())
				.bodyValue(dto)
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(false);
						
					} else if (response.statusCode().equals(HttpStatus.CREATED)) {
						return Mono.just(true);
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					}
					return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					
				})
				.block();
	}
	
	@Override
	public boolean deleteConnectionRecursive(UUID oId, UUID identityId, UUID requestedById) {
		return (boolean) this.client
				.delete()
				.uri(uriBuilder ->
						uriBuilder.path("/access/")
								.path(oId.toString())
								.queryParam("identityId", identityId)
								.queryParam("requestedById", requestedById)
								.build())
				.attributes(clientRegistrationId("keycloak-server"))
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.just(false);
						
					} else if (response.statusCode().equals(HttpStatus.NO_CONTENT)) {
						return Mono.just(true);
					} else if (response.statusCode().isError()) {
						return this.webClientExceptionHandler.handleWebClientException(response);
					}
					return Mono.error(new RuntimeException(response.statusCode() + " from rights service."));
					
				})
				.block();
	}
}
