package com.transit.backend.exeptions.handler.webclient;

import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.ForbiddenException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;


@Component
public class WebClientExceptionHandler {
	
	
	public Mono handleWebClientException(ClientResponse response) {
		if (response.statusCode().equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
			return response.bodyToMono(String.class).flatMap(body -> Mono.error(new UnprocessableEntityExeption(body)));
		} else if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
			return response.bodyToMono(String.class).flatMap(body -> Mono.error(new BadRequestException(body)));
		} else if (response.statusCode().equals(HttpStatus.FORBIDDEN)) {
			return response.bodyToMono(String.class).flatMap(body -> Mono.error(new ForbiddenException(body)));
		} else if (response.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
			return response.bodyToMono(String.class).flatMap(body -> Mono.error(new AccessDeniedException(body)));
		} else {
			return response.bodyToMono(String.class).flatMap(body -> Mono.error(new RuntimeException(body)));
		}
	}
}
