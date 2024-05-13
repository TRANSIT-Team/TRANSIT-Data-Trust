package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.PackageClassController;
import com.transit.backend.datalayers.controller.assembler.PackageClassAssembler;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.service.mapper.PackageClassMapper;
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
@Import(PackageClassController.class)
public class PackageClassControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	PackageClassController packageClassController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackageClassMapper packageClassMapper;
	@Autowired
	private PackageClassAssembler packageClassAssembler;
	private PackageClass packageClassTest;
	
	private String path;
	
	public PackageClassControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(packageClassController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(packageClassController);
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.packageClassTest = setupPackageClass(path);
		
		
	}
	
	PackageClass setupPackageClass(String path) throws JsonProcessingException {
		
		
		var packageClass = new PackageClass();
		packageClass.setName("Pappkarton_gepolstert");
		
		
		ResponseEntity<PackageClassDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageclasses", packageClass, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<PackageClassDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses/{id}", PackageClassDTO.class, response1.getBody().getId());
		
		return packageClassMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponsePackageClass() throws Exception {
		
		var packageItemClassPath = path + "/packageclasses/";
		Assertions.assertNotNull(this.packageClassTest);
		Assertions.assertNotNull(this.packageClassTest.getId());
		
		Assertions.assertEquals(this.packageClassTest, packageClassMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemClassPath + "{id}", PackageClassDTO.class, this.packageClassTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemClassPath + "{id}", String.class, this.packageClassTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValuesPackageClass(path, response);
		
	}
	
	
	void verifyJsonPackageNotNullValuesPackageClass(String path, final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packageItemClassDTO = packageClassAssembler.toModel(this.packageClassTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.packageClassTest.getId(), this.packageClassTest.getCreateDate(), this.packageClassTest.getModifyDate(), this.packageClassTest.isDeleted());
		
		Assertions.assertEquals(this.packageClassTest.getName(), root.get("name").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + packageItemClassDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	public void getReturnsNotFoundPackageClass() throws Exception {
		setupPackageClass(path);
		var packageItemClassPath = path + "/packageclasses/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemClassPath, String.class, this.packageClassTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageClasses").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<PackageClassDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemClassPath + "{id}", PackageClassDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
		
	}
	
	@Test
	public void postEmptyPackageClass() {
		log.info("postEmptyPackageClass");
		var packageItemClassPath = path + "/packageclasses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemClassPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postPackageClass() {
		log.info("postPackageClassNotFound");
		var packageItemClassPath = path + "/packageclasses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		PackageClass packageClass = new PackageClass();
		packageClass.setName("Palette-Einweg");
		
		HttpEntity<PackageClassDTO> entity = new HttpEntity<>(packageClassMapper.toDto(packageClass), headers);
		ResponseEntity<PackageClassDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemClassPath, entity, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putPackageClass() throws JsonProcessingException {
		log.info("putPackageClass");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packageItemClassPath = path + "/packageclasses/";
		ResponseEntity<PackageClassDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemClassPath + "{id}", PackageClassDTO.class, this.packageClassTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new PackageClass();
		
		dtoUpdate.setName("Palette_Plastik");
		dto.setName(dtoUpdate.getName());
		
		HttpEntity<PackageClassDTO> entity = new HttpEntity<>(packageClassMapper.toDto(dtoUpdate), headers);
		ResponseEntity<PackageClassDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(packageItemClassPath + this.packageClassTest.getId(), HttpMethod.PUT, entity, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.packageClassTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getName(), response2.getBody().getName());
	}
	
	@Test
	public void patchPackageClass() throws JsonProcessingException {
		log.info("patchPackageClass");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var packageItemClassPath = path + "/packageclasses/";
		
		
		String request = "{\"name\":\"Palette_begast\"}";
		var entity = new HttpEntity<String>(request, headers);
		//patch
		
		var response = template.exchange(packageItemClassPath + this.packageClassTest.getId(), HttpMethod.PATCH, entity, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<PackageClassDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses/{id}", PackageClassDTO.class, this.packageClassTest.getId());
		this.packageClassTest = packageClassMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.packageClassTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.packageClassTest.getName(), response.getBody().getName());
	}
	
	@Test
	public void deletePackageClass() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/packageclasses/" + this.packageClassTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		
		//ResponseEntity<PackageClassDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses/{id}", PackageClassDTO.class, this.packageClassTest.getId());
		//Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		//Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}
