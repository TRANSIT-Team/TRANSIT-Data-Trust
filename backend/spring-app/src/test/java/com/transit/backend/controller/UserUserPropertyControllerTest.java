package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.UserUserPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.UserUserPropertyAssemblerWrapper;
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
@Import(UserUserPropertyController.class)
public class UserUserPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	UserUserPropertyController userPropertyController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private UserPropertyMapper userPropertyMapper;
	@Autowired
	private UserUserPropertyAssemblerWrapper userPropertyAssembler;
	private UserProperty userPropertyTest;
	
	private String path;
	
	private UUID userId;
	
	private UsersResource usersResource;
	
	
	public UserUserPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
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
		Assertions.assertNotNull(userPropertyController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(userPropertyController);
		
		
		this.userPropertyTest = setupUserProperty();
		
		
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
		
		var userPropertyPath = this.path + "/userproperties/";
		Assertions.assertNotNull(this.userPropertyTest);
		Assertions.assertNotNull(this.userPropertyTest.getId());
		
		Assertions.assertEquals(this.userPropertyTest, userPropertyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertyPath + "{id}", UserPropertyDTO.class, this.userPropertyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertyPath + "{id}", String.class, this.userPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonUserNotNullValues(response);
		
	}
	
	
	void verifyJsonUserNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var userPropertyDTO = userPropertyAssembler.toModel(this.userPropertyTest, this.userId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.userPropertyTest.getId(), this.userPropertyTest.getCreateDate(), this.userPropertyTest.getModifyDate(), this.userPropertyTest.isDeleted());
		
		Assertions.assertEquals(this.userPropertyTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.userPropertyTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.userPropertyTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + userPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var userPropertyPath = this.path + "/userproperties/";
		var userProperty = new UserPropertyDTO();
		userProperty.setKey("testeigenschaft2");
		userProperty.setValue("F");
		userProperty.setType("string");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/userproperties", userProperty, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("userProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertyPath + "{id}", UserPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	public void postEmptyUserProperty() {
		log.info("postEmptyUserProperty");
		var userPropertyPath = this.path + "/userproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(userPropertyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postUserProperty() {
		log.info("postUserPropertyNotFound");
		var userPropertyPath = this.path + "/userproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		UserProperty userProperty = new UserProperty();
		userProperty.setKey("testeigenschaft3");
		userProperty.setValue("true");
		userProperty.setType("boolean");
		
		HttpEntity<UserPropertyDTO> entity = new HttpEntity<>(userPropertyMapper.toDto(userProperty), headers);
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(userPropertyPath, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putUserProperty() {
		log.info("putUserProperty");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var userPropertyPath = this.path + "/userproperties/";
		ResponseEntity<UserPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPropertyPath + "{id}", UserPropertyDTO.class, this.userPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new UserProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<UserPropertyDTO> entity = new HttpEntity<>(userPropertyMapper.toDto(dtoUpdate), headers);
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(userPropertyPath + this.userPropertyTest.getId(), HttpMethod.PUT, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.userPropertyTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	public void patchUserProperty() {
		log.info("patchUserProperty");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var userPropertyPath = this.path + "/userproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(userPropertyPath + this.userPropertyTest.getId(), HttpMethod.PATCH, entity, UserPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<UserPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties/{id}", UserPropertyDTO.class, this.userPropertyTest.getId());
		this.userPropertyTest = userPropertyMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.userPropertyTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.userPropertyTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	public void deleteUserProperty() {
		log.info("deleteUserProperty");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/userproperties/" + this.userPropertyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<UserPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/userproperties/{id}", UserPropertyDTO.class, this.userPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

