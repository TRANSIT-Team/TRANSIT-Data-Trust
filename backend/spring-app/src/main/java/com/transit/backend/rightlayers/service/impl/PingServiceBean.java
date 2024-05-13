package com.transit.backend.rightlayers.service.impl;

import com.transit.backend.exeptions.exeption.RightsServiceNotAvailableException;
import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import com.transit.backend.rightlayers.domain.ReturnPingDTO;
import com.transit.backend.rightlayers.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class PingServiceBean implements PingService {
	
	@Autowired
	private WebClient client;
	
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	@Override
	public void available() {
		
		
		try {
			this.client
					.get()
					.uri(uriBuilder ->
							uriBuilder.path("/ping")
									.build())
					.attributes(clientRegistrationId("keycloak-server"))
					.exchangeToMono(response -> {
						if (!response.statusCode().equals(HttpStatus.NOT_FOUND)) {
							return Mono.just(new ReturnPingDTO());
						} else if (response.statusCode().isError()) {
							return (Mono<ReturnPingDTO>) this.webClientExceptionHandler.handleWebClientException(response);
						} else if (response.statusCode().equals(HttpStatus.OK)) {
							return response.bodyToMono(new ParameterizedTypeReference<ReturnPingDTO>() {
							});
						} else {
							return Mono.error(new RuntimeException(String.valueOf(response.statusCode()) + " from rights service."));
						}
					})
					.block();
			
		} catch (Exception ex) {
			throw new RightsServiceNotAvailableException();
		}
		
	}
}
