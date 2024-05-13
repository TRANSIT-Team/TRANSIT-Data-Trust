package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.UserPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.UserPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.mapper.UserPropertyMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Import(UserPropertyController.class)
public class UserPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	UserPropertyController userPropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private UserPropertyMapper userPropertiesMapper;
	@Autowired
	private UserPropertyAssemblerWrapper userPropertiesAssembler;
	private UserProperty userPropertiesTest;
	
	private String path;
	
	
	private UUID userId;
	
	private UsersResource usersResource;
	
	@Autowired
	private UserPropertyMapper userPropertyMapper;
	
	public UserPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		this.usersResource = keycloakServiceManager.getUsersResource();
		
	}
	
	@AfterEach
	void delete() {
		if (!this.usersResource.search("test.tester.dummy@transit-project.de").isEmpty()) {
			this.usersResource.delete(this.usersResource.search("test.tester.dummy@transit-project.de").get(0).getId());
		}
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(userPropertiesController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(userPropertiesController);
		
		
		this.userPropertiesTest = setupUserProperty();
		
		
	}
	
	UserProperty setupUserProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var user = new UserDTO();
		String email = "test.tester.dummy@transit-project.de";
		if (!this.usersResource.search(email).isEmpty()) {
			this.usersResource.delete(this.usersResource.search(email).get(0).getId());
		}
		
		user.setFirstName("Test");
		user.setLastName("tester");
		user.setEmail(email);
		user.setEnabled(true);
		user.setCompanyId(super.getDefaultCompany().getId());
		user.setJobPosition("Programming");
		List<String> realmRoles = new ArrayList<>();
		realmRoles.add(TransitAuthorities.ADMIN_COMPANY.getStringValue());
		realmRoles.add(TransitAuthorities.CREATOR_ORDER.getStringValue());
		user.setRealmRoles(realmRoles);
		
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		ResponseEntity<UserDTO> response12 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "users/", user, UserDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response12.getStatusCode());
		
		String password = UUID.randomUUID().toString();
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);
		this.usersResource.get(Objects.requireNonNull(response12.getBody()).getKeycloakId().toString()).resetPassword(credential);
		getRestTemplateGenerator().setTestRestTemplate(getRestTemplateGenerator().getTestRestTemplate(user.getEmail(), password, String.valueOf(super.getRandomServerPort())));
		this.userId = response12.getBody().getId();
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/users/" + userId;
		
		var userProperty = new UserPropertyDTO();
		userProperty.setKey("Webpage");
		userProperty.setValue("test-firma.org");
		userProperty.setType("text");
		
		
		ResponseEntity<UserPropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/userproperties", userProperty, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties/{id}", UserPropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		return userPropertyMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var userPropertiesPath = this.path + "/userproperties/";
		Assertions.assertNotNull(this.userPropertiesTest);
		Assertions.assertNotNull(this.userPropertiesTest.getId());
		
		Assertions.assertEquals(this.userPropertiesTest, userPropertiesMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertiesPath + "{id}", UserPropertyDTO.class, this.userPropertiesTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/userproperties/" + "{id}", String.class, this.userPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//		Assertions.assertNotNull(response.getBody());
//		log.info(response.getBody());
//		verifyJsonUserNotNullValues(response);
//
	}
	
	
	void verifyJsonUserNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var userPropertiesDTO = userPropertiesAssembler.toModel(this.userPropertiesTest, this.userId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.userPropertiesTest.getId(), this.userPropertiesTest.getCreateDate(), this.userPropertiesTest.getModifyDate(), this.userPropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.userPropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.userPropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.userPropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		
		
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + userPropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var userPropertiesPath = this.path + "/userproperties/";
		var userProperties = new UserPropertyDTO();
		userProperties.setKey("Web");
		userProperties.setValue("test.com");
		userProperties.setType("text");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/users/" + this.userId + "/userproperties", userProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/users/" + this.userId + "/userproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("userProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertiesPath + "{id}", UserPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/userproperties/" + this.userPropertiesTest.getId(), String.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	
	@Test
	public void putUserProperties() {
		log.info("putUserProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var userPropertiesPath = this.path + "/userproperties/";
		ResponseEntity<UserPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertiesPath + "{id}", UserPropertyDTO.class, this.userPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new UserProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<UserPropertyDTO> entity = new HttpEntity<>(userPropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(userPropertiesPath + this.userPropertiesTest.getId(), HttpMethod.PUT, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.userPropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
		ResponseEntity<UserPropertyDTO> responseUnAuth = getRestTemplateGenerator().getTestRestTemplate().exchange(getBASE_PATH() + getRandomServerPort() + "/api/v1/userproperties/" + this.userPropertiesTest.getId(), HttpMethod.PUT, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseUnAuth.getStatusCode());
	}
	
	@Test
	public void patchUserProperties() {
		log.info("patchUserProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var userPropertiesPath = this.path + "/userproperties/";
		
		
		String request = "{\"value\":\"test-test.com\"}";
		var entity = new HttpEntity<>(request, headers);
		//patch
		
		var response = template.exchange(userPropertiesPath + this.userPropertiesTest.getId(), HttpMethod.PATCH, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("test-test.com", response.getBody().getValue());
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties/{id}", UserPropertyDTO.class, this.userPropertiesTest.getId());
		this.userPropertiesTest = userPropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.userPropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.userPropertiesTest.getValue(), response.getBody().getValue());
		
		ResponseEntity<String> responseUnAuth = getRestTemplateGenerator().getTestRestTemplate().exchange(getBASE_PATH() + getRandomServerPort() + "/api/v1/userproperties/" + this.userPropertiesTest.getId(), HttpMethod.PATCH, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseUnAuth.getStatusCode());
	}
	
	@Test
	public void deleteUserProperties() {
		log.info("deleteUserItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/userproperties/" + this.userPropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<UserPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties/{id}", UserPropertyDTO.class, this.userPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		
		ResponseEntity<Void> responseUnAuth = getRestTemplateGenerator().getTestRestTemplate().exchange(getBASE_PATH() + getRandomServerPort() + "/api/v1/userproperties/" + this.userPropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseUnAuth.getStatusCode());
	}
	
	
}

