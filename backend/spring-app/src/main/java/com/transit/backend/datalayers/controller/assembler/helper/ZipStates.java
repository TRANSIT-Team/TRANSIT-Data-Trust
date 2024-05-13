package com.transit.backend.datalayers.controller.assembler.helper;

import com.transit.backend.exeptions.handler.webclient.WebClientExceptionHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Getter
public class ZipStates {
	
	private WebClient webClient;
	
	@Autowired
	private WebClientExceptionHandler webClientExceptionHandler;
	
	private List<Zipcodesgermanystates> zipStates;
	
	public ZipStates() {
		HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
		this.webClient = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.exchangeStrategies(ExchangeStrategies.builder().codecs(
						configurer -> configurer.defaultCodecs().maxInMemorySize(-1)
				).build())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.baseUrl("https://api.transit-project.de/geoservice/v1/")
				.build();
		zipStates = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			try {
				zipStates = webClient
						.get()
						.uri(uriBuilder ->
								//		https://api.transit-project.de/geoservice/v1
								uriBuilder.path("zipstates/").build()
						).exchangeToMono(
								response -> {
									if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
										return Mono.just(new ArrayList<Zipcodesgermanystates>());
									} else if (response.statusCode().isError()) {
										return (Mono<List<Zipcodesgermanystates>>) this.webClientExceptionHandler.handleWebClientException(response);
									} else if (response.statusCode().equals(HttpStatus.OK)) {
										return response.bodyToMono(new ParameterizedTypeReference<List<Zipcodesgermanystates>>() {
										});
									} else {
										return Mono.error(new RuntimeException(response.statusCode() + " from geoservice."));
									}
								})
						.block();
				
				
				if (zipStates != null && !zipStates.isEmpty()) {
					break;
				} else {
					TimeUnit.SECONDS.sleep(2);
				}
				
			} catch (InterruptedException e) {
			}
		}
	}
	
	public Point getGermanyMiddle() {
		Point tempPoint = new GeometryFactory().createPoint(new Coordinate(10.4541194, 51.1642292));
		return tempPoint;
	}
}
