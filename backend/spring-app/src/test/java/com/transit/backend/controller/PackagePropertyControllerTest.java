package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.PackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.PackagePropertyAssembler;
import com.transit.backend.datalayers.controller.dto.PackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.service.mapper.PackagePropertyMapper;
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
@Import(PackagePropertyController.class)
public class PackagePropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	PackagePropertyController packagePropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackagePropertyMapper packagePropertyMapper;
	@Autowired
	private PackagePropertyAssembler packagePropertiesAssembler;
	private PackageProperty packagePropertyTest;
	
	private String path;
	
	public PackagePropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
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
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.packagePropertyTest = setupPackageProperty(path);
		
		
	}
	
	PackageProperty setupPackageProperty(String path) {
		
		
		var packageProperties = new PackagePropertyDTO();
		packageProperties.setKey("Gefahrgutklasse");
		packageProperties.setDefaultValue("2");
		packageProperties.setType("int");
		
		
		ResponseEntity<PackagePropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageproperties", packageProperties, PackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<PackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageproperties/{id}", PackagePropertyDTO.class, response1.getBody().getId());
		
		return packagePropertyMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var packagePropertiesPath = path + "/packageproperties/";
		Assertions.assertNotNull(this.packagePropertyTest);
		Assertions.assertNotNull(this.packagePropertyTest.getId());
		
		Assertions.assertEquals(this.packagePropertyTest, packagePropertyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePropertyDTO.class, this.packagePropertyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", String.class, this.packagePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(path, response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(String path, final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packagePropertiesDTO = packagePropertiesAssembler.toModel(this.packagePropertyTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.packagePropertyTest.getId(), this.packagePropertyTest.getCreateDate(), this.packagePropertyTest.getModifyDate(), this.packagePropertyTest.isDeleted());
		
		Assertions.assertEquals(this.packagePropertyTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.packagePropertyTest.getDefaultValue(), root.get("defaultValue").asText());
		Assertions.assertEquals(this.packagePropertyTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + packagePropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		setupPackageProperty(path);
		var packagePropertiesPath = path + "/packageproperties/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath, String.class, this.packagePropertyTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<PackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	public void postEmptyPackageProperties() {
		log.info("postEmptyPackageProperties");
		var packagePropertiesPath = path + "/packageproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packagePropertiesPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postPackageProperties() {
		log.info("postPackagePropertiesNotFound");
		var packagePropertiesPath = path + "/packageproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		PackageProperty packageProperty = new PackageProperty();
		packageProperty.setKey("Zerbrechlich");
		packageProperty.setDefaultValue("true");
		packageProperty.setType("boolean");
		
		HttpEntity<PackagePropertyDTO> entity = new HttpEntity<>(packagePropertyMapper.toDto(packageProperty), headers);
		ResponseEntity<PackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packagePropertiesPath, entity, PackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putPackageProperties() {
		log.info("putPackageProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packagePropertiesPath = path + "/packageproperties/";
		ResponseEntity<PackagePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packagePropertiesPath + "{id}", PackagePropertyDTO.class, this.packagePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new PackageProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setDefaultValue("5");
		dto.setDefaultValue(dtoUpdate.getDefaultValue());
		dtoUpdate.setType(dto.getType());
		
		HttpEntity<PackagePropertyDTO> entity = new HttpEntity<>(packagePropertyMapper.toDto(dtoUpdate), headers);
		ResponseEntity<PackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(packagePropertiesPath + this.packagePropertyTest.getId(), HttpMethod.PUT, entity, PackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.packagePropertyTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getDefaultValue(), response2.getBody().getDefaultValue());
	}
	
	@Test
	public void patchPackageProperties() {
		log.info("patchPackageProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var packagePropertiesPath = path + "/packageproperties/";
		
		
		String request = "{\"defaultValue\":\"4\"}";
		var entity = new HttpEntity<String>(request, headers);
		//patch
		
		var response = template.exchange(packagePropertiesPath + this.packagePropertyTest.getId(), HttpMethod.PATCH, entity, PackagePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getDefaultValue());
		ResponseEntity<PackagePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageproperties/{id}", PackagePropertyDTO.class, this.packagePropertyTest.getId());
		this.packagePropertyTest = packagePropertyMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.packagePropertyTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.packagePropertyTest.getDefaultValue(), response.getBody().getDefaultValue());
	}
	
	@Test
	public void deletePackageProperties() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/packageproperties/" + this.packagePropertyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<PackagePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageproperties/{id}", PackagePropertyDTO.class, this.packagePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

