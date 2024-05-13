package com.transit.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;


@Component
@Getter
@Setter
@Slf4j
public class RestTemplateGenerator {
	
	ObjectMapper objectMapper;
	private TestRestTemplate testRestTemplate;
	private RestTemplate restTemplate;
	private Environment env;
	
	
	@Autowired
	public RestTemplateGenerator(Environment env, RestTemplateBuilder builder, ObjectMapper objectMapper) throws JsonProcessingException {
		this.objectMapper = objectMapper;
		this.restTemplate = builder.build();
		this.env = env;
		this.testRestTemplate = new TestRestTemplate();
		
		this.testRestTemplate.getRestTemplate().setInterceptors(
				Collections.singletonList((request, body, execution) -> {
					request.getHeaders()
							.add(HttpHeaders.AUTHORIZATION, "Bearer " + login(null, null, null));
					return execution.execute(request, body);
				}));
	}
	
	public String login(String username, String password, String port) throws JsonProcessingException {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		if (username == null) {
			map.add("username", env.getProperty("keycloak.test.username"));
		} else {
			map.add("username", username);
		}
		if (password == null) {
			map.add("password", env.getProperty("keycloak.test.password"));
		} else {
			map.add("password", password);
		}
		if (port == null) {
			map.add("port", "8080");
		} else {
			map.add("port", port);
		}
		map.add("client_id", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-id"));
		map.add("grant_type", "password");
		map.add("client_secret", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-secret"));
		map.add("scope", "openid");
		if (port == null) {
			map.add("redirect_uri", "http://localhost:8080");
		} else {
			map.add("redirect_uri", "http://localhost:" + port);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		
		String response = restTemplate.postForObject(Objects.requireNonNull(env.getProperty("spring.security.oauth2.client.provider.keycloak.token-uri")), request, String.class);
		
		JsonNode root = objectMapper.readTree(response);
		
		return root.get("access_token").asText();
		
		
	}
	
	public RestTemplateGenerator(Environment env, RestTemplateBuilder builder, ObjectMapper objectMapper, String username, String password, String port) throws JsonProcessingException {
		this.objectMapper = objectMapper;
		this.restTemplate = builder.build();
		this.env = env;
		this.testRestTemplate = new TestRestTemplate();
		this.testRestTemplate.getRestTemplate().setInterceptors(
				Collections.singletonList((request, body, execution) -> {
					request.getHeaders()
							.add(HttpHeaders.AUTHORIZATION, "Bearer " + login(username, password, port));
					return execution.execute(request, body);
				}));
	}
	
	public TestRestTemplate getTestRestTemplate(String username, String password, String port) {
		var template = new TestRestTemplate();
		template.getRestTemplate().setInterceptors(
				Collections.singletonList((request, body, execution) -> {
					request.getHeaders()
							.add(HttpHeaders.AUTHORIZATION, "Bearer " + login(username, password, port));
					return execution.execute(request, body);
				}));
		return template;
	}
	
	
}


