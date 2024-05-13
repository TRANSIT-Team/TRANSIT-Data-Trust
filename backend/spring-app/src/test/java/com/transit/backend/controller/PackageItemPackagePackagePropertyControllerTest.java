package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.PackageItemPackagePackagePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.UUID;

@Slf4j
@Import(PackageItemPackagePackagePropertyController.class)
public class PackageItemPackagePackagePropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	PackageItemPackagePackagePropertyController packagePropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackagePackagePropertyMapper packagePropertiesMapper;
	@Autowired
	private PackageItemPackagePackagePropertyAssemblerWrapper packagePropertiesAssembler;
	private PackagePackageProperty packagePropertiesTest;
	
	private String path;
	
	private UUID packageItemId;
	
	public PackageItemPackagePackagePropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(packagePropertiesController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(packagePropertiesController);
		
		
		this.packagePropertiesTest = setupPackageProperty();
		
		
	}
	
	PackagePackageProperty setupPackageProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		var packageClass = new PackageClass();
		packageClass.setName("Pappkarton_gepolstert");
		
		
		ResponseEntity<PackageClassDTO> responseC = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/packageclasses", packageClass, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, responseC.getStatusCode());
		String requestJson = "{\"packageClass\":{\"id\":\"" + responseC.getBody().getId() + "\"}}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/packageitems/", entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packageId = UUID.fromString(root.get("id").asText());
		this.packageItemId = packageId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/packageitems/" + packageId;
		
		var packageProperties = new PackagePackagePropertyDTO();
		packageProperties.setKey("Gefahrgutklasse");
		packageProperties.setValue("2");
		packageProperties.setType("int");
		
		
		ResponseEntity<PackagePackagePropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/packagepackageproperties", packageProperties, PackagePackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<PackagePackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/packagepackageproperties/{id}", PackagePackagePropertyDTO.class, response1.getBody().getId());
		return packagePropertiesMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		Assertions.assertNotNull(this.packagePropertiesTest);
		Assertions.assertNotNull(this.packagePropertiesTest.getId());
		
		Assertions.assertEquals(this.packagePropertiesTest, packagePropertiesMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePackagePropertyDTO.class, this.packagePropertiesTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", String.class, this.packagePropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packagePropertiesDTO = packagePropertiesAssembler.toModel(this.packagePropertiesTest, this.packageItemId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.packagePropertiesTest.getId(), this.packagePropertiesTest.getCreateDate(), this.packagePropertiesTest.getModifyDate(), this.packagePropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.packagePropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.packagePropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.packagePropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + packagePropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		var packageProperties = new PackagePackagePropertyDTO();
		packageProperties.setKey("Gefahrengrad");
		packageProperties.setValue("F");
		packageProperties.setType("string");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/packagepackageproperties", packageProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/packagepackageproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packagePackageProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<PackagePackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePackagePropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	public void postEmptyPackageProperties() {
		log.info("postEmptyPackageProperties");
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packagePropertiesPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postPackageProperties() {
		log.info("postPackagePropertiesNotFound");
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		PackagePackageProperty packageProperties = new PackagePackageProperty();
		packageProperties.setKey("Zerbrechlich");
		packageProperties.setValue("true");
		packageProperties.setType("boolean");
		
		HttpEntity<PackagePackagePropertyDTO> entity = new HttpEntity<>(packagePropertiesMapper.toDto(packageProperties), headers);
		ResponseEntity<PackagePackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packagePropertiesPath, entity, PackagePackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putPackageProperties() {
		log.info("putPackageProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		ResponseEntity<PackagePackagePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePackagePropertyDTO.class, this.packagePropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new PackagePackageProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<PackagePackagePropertyDTO> entity = new HttpEntity<>(packagePropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<PackagePackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(packagePropertiesPath + this.packagePropertiesTest.getId(), HttpMethod.PUT, entity, PackagePackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.packagePropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	public void patchPackageProperties() {
		log.info("patchPackageProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var packagePropertiesPath = this.path + "/packagepackageproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(packagePropertiesPath + this.packagePropertiesTest.getId(), HttpMethod.PATCH, entity, PackagePackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<PackagePackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/packagepackageproperties/{id}", PackagePackagePropertyDTO.class, this.packagePropertiesTest.getId());
		this.packagePropertiesTest = packagePropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.packagePropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.packagePropertiesTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	public void deletePackageProperties() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/packagepackageproperties/" + this.packagePropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		
		//	ResponseEntity<PackagePackagePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/packagepackageproperties/{id}", PackagePackagePropertyDTO.class, this.packagePropertiesTest.getId());
		//	Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		//	Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

