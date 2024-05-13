package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.UserController;
import com.transit.backend.datalayers.controller.UserPropertyController;
import com.transit.backend.datalayers.controller.assembler.UserAssembler;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserIdDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.mapper.UserTransferMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import com.transit.backend.transferentities.UserTransferObject;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static java.util.UUID.randomUUID;

@Slf4j
@Import({UserController.class, UserPropertyController.class})
public class UserControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	UserController userController;
	
	
	@InjectMocks
	UserPropertyController userUserPropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private UserTransferMapper userMapper;
	@Autowired
	private UserAssembler userAssembler;
	
	
	private UserTransferObject userTest;
	
	private String path;
	
	private int count;
	
	private KeycloakServiceManager keycloakServiceManager;
	private UsersResource usersResource;
	
	public UserControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		this.keycloakServiceManager = keycloakServiceManager;
		this.usersResource = this.keycloakServiceManager.getUsersResource();
		
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(userController);
		
	}
	
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(userController);
		
		Assertions.assertNotNull(userUserPropertyController);
		this.count = 3;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.userTest = setupUser(path);
		
		
	}
	
	UserTransferObject setupUser(String path) {
		var user = new UserDTO();
		String email = "test.tester.dummy@transit-project.de";
		if (!this.usersResource.search(email).isEmpty()) {
			this.usersResource.delete(this.usersResource.search(email).get(0).getId());
		}
		
		user.setFirstName("Test");
		user.setLastName("tester");
		user.setEmail(email);
		user.setEnabled(true);
		
		
		var userProperties1 = new UserPropertyDTO();
		userProperties1.setKey("Alter");
		userProperties1.setValue("500");
		userProperties1.setType("int");
		
		var userProperties2 = new UserPropertyDTO();
		userProperties2.setKey("Geschlecht");
		userProperties2.setValue("weibllich");
		userProperties2.setType("string");
		SortedSet<UserPropertyDTO> userProperties = new TreeSet<UserPropertyDTO>();
		userProperties.add(userProperties1);
		userProperties.add(userProperties2);
		
		user.setJobPosition("Programming");
		List<String> realmRoles = new ArrayList<>();
		realmRoles.add(TransitAuthorities.ADMIN_COMPANY.getStringValue());
		realmRoles.add(TransitAuthorities.CREATOR_ORDER.getStringValue());
		user.setRealmRoles(realmRoles);
		user.setUserProperties(userProperties);
		
		
		user.setCompanyId(super.getDefaultCompany().getId());
		
		
		ResponseEntity<UserDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/users/", user, UserDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		
		String password = UUID.randomUUID().toString();
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);
		this.usersResource.get(Objects.requireNonNull(response1.getBody()).getKeycloakId().toString()).resetPassword(credential);
		getRestTemplateGenerator().setTestRestTemplate(getRestTemplateGenerator().getTestRestTemplate(user.getEmail(), password, String.valueOf(super.getRandomServerPort())));
		return userMapper.toEntity(response1.getBody());
	}
	
	@AfterEach
	void delete() {
		if (!this.usersResource.search(this.userTest.getUserRepresentation().getUsername()).isEmpty()) {
			this.usersResource.delete(this.userTest.getUserRepresentation().getId());
		}
	}
	
	@Test
	public void getReturnsCorrectResponseUserItem() throws Exception {
		
		var userPath = path + "/users/";
		Assertions.assertNotNull(this.userTest);
		Assertions.assertNotNull(this.userTest.getUser());
		Assertions.assertNotNull(this.userTest.getUserRepresentation());
		
		//Assertions.assertEquals(this.userTest, userMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPath + "{id}", UserDTO.class, this.userTest.getUser().getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPath + "{id}", String.class, this.userTest.getUser().getId());
		ResponseEntity<UserDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(userPath + "{id}", UserDTO.class, this.userTest.getUser().getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonUserNotNullValuesUserItem(path, response, response2);
		
	}
	
	void verifyJsonUserNotNullValuesUserItem(String path, final ResponseEntity<String> response, ResponseEntity<UserDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var userDTO = userAssembler.toModel(this.userTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.userTest.getUser().getId(), this.userTest.getUser().getCreateDate(), this.userTest.getUser().getModifyDate(), this.userTest.getUser().isDeleted());
		
		Assertions.assertEquals(this.userTest.getUser().getKeycloakId(), UUID.fromString(root.get("keycloakId").asText()));
		Assertions.assertEquals(this.userTest.getUserRepresentation().getFirstName(), root.get("firstName").asText());
		Assertions.assertEquals(this.userTest.getUserRepresentation().getLastName(), root.get("lastName").asText());
		Assertions.assertEquals(this.userTest.getUserRepresentation().getEmail(), root.get("email").asText());
		List<String> realmRoles = new ArrayList<>();
		if (root.get("realmRoles").isArray()) {
			for (JsonNode item : root.get("realmRoles")) {
				realmRoles.add(item.asText());
			}
		}
		Assertions.assertTrue(realmRoles.size() == this.userTest.getUserRepresentation().getRealmRoles().size());
		
		
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + userDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
		verifyJsonUserUserPropertiesNotNullValues(path, root.get("userProperties"), userDTO);
		
	}
	
	void verifyJsonUserUserPropertiesNotNullValues(String path, JsonNode node, UserDTO userDTO) throws Exception {
		
		var userPropertiesIterator = this.userTest.getUser().getUserProperties().iterator();
		for (int z = 0; z < this.userTest.getUser().getUserProperties().size(); z++) {
			UserProperty userProperty = userPropertiesIterator.next();
			UserPropertyDTO userPropertyDTO = userDTO.getUserProperties().stream().filter(userPropertiesDTO1 -> userPropertiesDTO1.getId().equals(userProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getUserPropertiesById = filter(
					where("id").is(userProperty.getId().toString())
			);
			log.info(node.toPrettyString());
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getUserPropertiesById);
			JsonNode nodeUserProperties = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodeUserProperties, userProperty.getId(), userProperty.getCreateDate(), userProperty.getModifyDate(), userProperty.isDeleted());
			Assertions.assertEquals(userProperty.getKey(), nodeUserProperties.get("key").asText());
			Assertions.assertEquals(userProperty.getValue(), nodeUserProperties.get("value").asText());
			Assertions.assertEquals(userProperty.getType(), nodeUserProperties.get("type").asText());
			JsonNode self = nodeUserProperties.get("_links").get("self");
			Assertions.assertEquals(Link.of(path + userPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var usersPath = path + "/users/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath, String.class, this.userTest.getUser());
		
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("users").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = randomUUID();
		}
		ResponseEntity<UserDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + "{id}", UserDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	public void postEmptyUser() throws JsonProcessingException {
		deleteIfExists("maxi.mustermann@transit-project.de");
		log.info("postEmptyUser");
		var usersPath = path + "/users/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(usersPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		
		requestJson = "{" +
				"\"companyId\": \"" + super.getDefaultCompany().getId() + "\"," +
				"\"firstName\": \"Maxi\"," +
				"\"lastName\": \"Mustermann\"," +
				"\"email\": \"maxi.mustermann@transit-project.de\"," +
				"\"realmRoles\": [\"workerWarehouse\"]," +
				"\"groups\": null," +
				"\"enabled\": true," +
				"\"userProperties\": [" +
				"{" +
				"\"key\": \"Geschlecht\"," +
				"   \"value\": \"weiblich\"," +
				"  \"type\": \"text\"" +
				"}" +
				"]," +
				"\"jobPosition\":\"Lagerarbeiter\"" +
				"}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(usersPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		//Test filter by empty UserProperties
		JsonNode root = objectMapper.readTree(response.getBody());
		JsonNode self = root.get("_links").get("self");
		
		ResponseEntity<UserDTO> responseD = getRestTemplateGenerator().getTestRestTemplate().getForEntity(self.get("href").asText() + "?filter=userProperties.deleted==false", UserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, responseD.getStatusCode());
		ResponseEntity<String> responseE = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + "?filter= userProperties.deleted==false;deleted==false", String.class);
		Assertions.assertEquals(HttpStatus.OK, responseE.getStatusCode());
		deleteIfExists("maxi.mustermann@transit-project.de");
	}
	
	private void deleteIfExists(String username) {
		if (!this.usersResource.search(username).isEmpty()) {
			this.usersResource.delete(this.usersResource.search(username).get(0).getId());
			
		}
	}
	
	@Test
	public void postUser() throws JsonProcessingException {
		deleteIfExists("maxi.mustermann@transit-project.de");
		log.info("postUser");
		var usersPath = path + "/users/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{" +
				"\"companyId\": \"" + randomUUID() + "\"," +
				"\"firstName\": \"Maxi\"," +
				"\"lastName\": \"Mustermann\"," +
				"\"email\": \"maxi.mustermann@transit-project.de\"," +
				"\"realmRoles\": [\"workerWarehouse\"]," +
				"\"groups\": null," +
				"\"enabled\": true," +
				"\"userProperties\": [" +
				"{" +
				"\"key\": \"Geschlecht\"," +
				"   \"value\": \"weiblich\"," +
				"  \"type\": \"text\"" +
				"}" +
				"]," +
				"\"jobPosition\":\"Lagerarbeiter\"" +
				"}";
		
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(usersPath, entity, String.class);
		
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		deleteIfExists("maxi.mustermann@transit-project.de");
		requestJson = "{" +
				"\"companyId\": \"" + super.getDefaultCompany().getId() + "\"," +
				"\"firstName\": \"Maxi\"," +
				"\"lastName\": \"Mustermann\"," +
				"\"email\": \"maxi.mustermann@transit-project.de\"," +
				"\"realmRoles\": [\"workerWarehouse\"]," +
				"\"groups\": null," +
				"\"enabled\": true," +
				"\"userProperties\": [" +
				"{" +
				"\"key\": \"Geschlecht\"," +
				"   \"value\": \"weiblich\"," +
				"  \"type\": \"text\"" +
				"}" +
				"]," +
				"\"jobPosition\":\"Lagerarbeiter\"" +
				"}";
		entity = new HttpEntity<>(requestJson, headers);
		response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(usersPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		deleteIfExists("maxi.mustermann@transit-project.de");
	}
	
	@Test
	public void putUserWithUserClass() throws JsonProcessingException {
		log.info("putUserUserClass");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var usersPath = path + "/users/";
		ResponseEntity<UserDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/users/{id}", UserDTO.class, this.userTest.getUser().getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		
		dto.setId(null);
		SortedSet<UserPropertyDTO> userProperties = new TreeSet<>();
		var property1 = new UserPropertyDTO();
		property1.setKey("Key");
		property1.setValue("Value");
		property1.setType("Type");
		userProperties.add(property1);
		this.count += 1;
		
		dto.setUserProperties(userProperties);
		dto.setCreateDate(null);
		dto.setModifyDate(null);
		dto.setLastModifiedBy(null);
		dto.setCreatedBy(null);
		dto.setDeleted(false);
		dto.setJobPosition("otherposition");
		dto.removeLinks();
		
		
		HttpEntity<UserDTO> entity;
		ResponseEntity<UserDTO> response2;
		
		entity = new HttpEntity<>(dto, headers);
		Assertions.assertEquals(1, entity.getBody().getUserProperties().size());
		//put
		response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PUT, entity, UserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + this.userTest.getUser().getId(), UserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getCompanyId(), response2.getBody().getCompanyId());
		Assertions.assertEquals(this.userTest.getUser().getId(), response2.getBody().getId());
		Assertions.assertEquals(1, response2.getBody().getUserProperties().size());
		
		Assertions.assertEquals(4, this.count);
		for (var property : response2.getBody().getUserProperties()) {
			if (!property.getKey().equals(property1.getKey())) {
				Assertions.assertTrue(property.isDeleted());
			}
		}
		
	}
	
	@Test
	public void patchUser() throws IOException {
		log.info("patchUser");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var usersPath = path + "/users/";
		ResponseEntity<UserDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/users/{id}", UserDTO.class, this.userTest.getUser().getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		
		dto.setJobPosition("otherpart2");
		String request = "{\"jobPosition\":\"" + dto.getJobPosition() + "\"}";
		HttpEntity<String> entity = new HttpEntity<String>(request, headers);
		ResponseEntity<UserDTO> response2;
		//patch
		
		response2 = template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, UserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getJobPosition(), response2.getBody().getJobPosition());
		Assertions.assertEquals(this.userTest.getUser().getId(), response2.getBody().getId());
		
		request = "{\"jobPosition\":\"" + dto.getJobPosition() + "\"," +
				"\n" +
				"        \"userProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"5344\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, UserDTO.class);
		request = "{\"jobPosition\":\"" + dto.getJobPosition() + "\"," +
				"\n" +
				"        \"userProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"54\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, UserDTO.class);
		
		for (var i : response2.getBody().getUserProperties()) {
			for (var j : response3.getBody().getUserProperties()) {
				if (i.getKey() == j.getKey()) {
					Assertions.assertEquals(i.getId(), j.getId());
					Assertions.assertEquals(i.getType(), j.getType());
					Assertions.assertEquals(i.isDeleted(), j.isDeleted());
					Assertions.assertEquals(i.getCreateDate(), j.getCreateDate());
					Assertions.assertNotEquals(i.getValue(), j.getValue());
					Assertions.assertNotEquals(i.getModifyDate(), j.getModifyDate());
				}
			}
		}
		request = "{\"jobPosition\":\"" + dto.getJobPosition() + "\"," +
				"\n" +
				"        \"userProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"5344\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"mini\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		response3 = template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, UserDTO.class);
		Assertions.assertEquals(3, response3.getBody().getUserProperties().size());
		request = "{\"jobPosition\":\"" + dto.getJobPosition() + "\"," +
				"\n" +
				"        \"userProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"54\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, UserDTO.class);
		Assertions.assertEquals(2, response3.getBody().getUserProperties().size());
		
		template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + "?filter=userProperties.deleted==all", String.class);
		
		JsonNode root = objectMapper.readTree(response4.getBody());
		root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("users");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<UserDTO>>() {
		});
		List<UserDTO> usersDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		for (var j : usersDTOS) {
			if (j.getId().equals(this.userTest.getUser().getId())) {
				for (var i : j.getUserProperties()) {
					log.info(i.getKey());
					if (i.isDeleted()) {
						log.info(i.getKey());
						if (i.getKey().equals("Geschlecht") || i.getKey().equals("Alter")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
						}
					} else {
						notdel += 1;
					}
					
				}
			}
		}
		log.info(node.toPrettyString());
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(2, notdel);
		
		template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + "?filter=userProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("users");
		reader = objectMapper.readerFor(new TypeReference<List<UserDTO>>() {
		});
		usersDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : usersDTOS) {
			if (j.getId().equals(this.userTest.getUser().getId())) {
				for (var i : j.getUserProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Geschlecht") || i.getKey().equals("Alter")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
						}
					} else {
						notdel += 1;
					}
				}
				
			}
		}
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(0, notdel);
		
		template.exchange(usersPath + this.userTest.getUser().getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(usersPath + "?filter=userProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("users");
		reader = objectMapper.readerFor(new TypeReference<List<UserDTO>>() {
		});
		usersDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : usersDTOS) {
			if (j.getId().equals(this.userTest.getUser().getId())) {
				for (var i : j.getUserProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Geschlecht") || i.getKey().equals("Alter")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
							
						}
					} else {
						notdel += 1;
					}
					
				}
			}
		}
		Assertions.assertEquals(0, del);
		Assertions.assertEquals(2, notdel);
	}
	
	@Test
	public void deleteUser() {
		log.info("deleteUser");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/users/" + this.userTest.getUser().getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		this.restTemplateGenerator.setTestRestTemplate(getRestTemplateGenerator().getTestRestTemplate(super.getDefaultUserRepresentation().getUsername(), super.getTestpasswordDefault(), String.valueOf(super.getRandomServerPort())));
		
		ResponseEntity<UserDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/users/{id}", UserDTO.class, this.userTest.getUser().getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getUserProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
	@Test
	public void getUserId() {
		log.info("getUserId");
		ResponseEntity<UserIdDTO> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/users/getId/" + this.userTest.getUserRepresentation().getId(), HttpMethod.GET, null, UserIdDTO.class);
		Assertions.assertNotNull(response.getBody());
		Assertions.assertNotNull(response.getBody().getUserId());
		Assertions.assertEquals(this.userTest.getUser().getId(), response.getBody().getUserId());
	}
	
	@Test
	public void getUser() {
		log.info("getUser");
		ResponseEntity<UserDTO> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/users/getSelf/" + this.userTest.getUserRepresentation().getId(), HttpMethod.GET, null, UserDTO.class);
		Assertions.assertNotNull(response.getBody());
		Assertions.assertNotNull(response.getBody().getId());
		Assertions.assertEquals(this.userTest.getUser().getId(), response.getBody().getId());
	}
	
}
