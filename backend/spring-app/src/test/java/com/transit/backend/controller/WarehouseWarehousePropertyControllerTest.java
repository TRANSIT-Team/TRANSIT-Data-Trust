package com.transit.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.WarehouseWarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehousePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
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
@Import(WarehouseWarehousePropertyController.class)
public class WarehouseWarehousePropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	WarehouseWarehousePropertyController warehousePropertyController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	@Autowired
	private WarehouseWarehousePropertyAssemblerWrapper warehousePropertyAssembler;
	private WarehouseProperty warehousePropertyTest;
	
	private String path;
	
	private UUID warehouseId;
	
	public WarehouseWarehousePropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(warehousePropertyController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(warehousePropertyController);
		
		
		this.warehousePropertyTest = setupPackageProperty();
		
		
	}
	
	WarehouseProperty setupPackageProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehouse = new Warehouse();
		warehouse.setName("testWarehouse");
		warehouse.setCapacity(12341324l);
		
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		ResponseEntity<WarehouseDTO> response5 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses", warehouse, WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response5.getStatusCode());
		
		var warehouseId = response5.getBody().getId();
		this.warehouseId = warehouseId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/warehouses/" + warehouseId;
		
		var warehouseProperty = new WarehousePropertyDTO();
		warehouseProperty.setKey("Webpage");
		warehouseProperty.setValue("test-firma.org");
		warehouseProperty.setType("text");
		
		
		ResponseEntity<WarehousePropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/warehouseproperties", warehouseProperty, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		return warehousePropertyMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		Assertions.assertNotNull(this.warehousePropertyTest);
		Assertions.assertNotNull(this.warehousePropertyTest.getId());
		
		Assertions.assertEquals(this.warehousePropertyTest, warehousePropertyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertyPath + "{id}", WarehousePropertyDTO.class, this.warehousePropertyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertyPath + "{id}", String.class, this.warehousePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var warehousePropertyDTO = warehousePropertyAssembler.toModel(this.warehousePropertyTest, this.warehouseId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.warehousePropertyTest.getId(), this.warehousePropertyTest.getCreateDate(), this.warehousePropertyTest.getModifyDate(), this.warehousePropertyTest.isDeleted());
		
		Assertions.assertEquals(this.warehousePropertyTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.warehousePropertyTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.warehousePropertyTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + warehousePropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	void getReturnsNotFound() throws Exception {
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		var warehouseProperty = new WarehousePropertyDTO();
		warehouseProperty.setKey("testeigenschaft2");
		warehouseProperty.setValue("F");
		warehouseProperty.setType("string");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/warehouseproperties", warehouseProperty, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("warehouseProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertyPath + "{id}", WarehousePropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	void postEmptyWarehouseProperty() {
		log.info("postEmptyWarehouseProperty");
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehousePropertyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	void postWarehouseProperty() {
		log.info("postWarehousePropertyNotFound");
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		WarehouseProperty warehouseProperty = new WarehouseProperty();
		warehouseProperty.setKey("testeigenschaft3");
		warehouseProperty.setValue("true");
		warehouseProperty.setType("boolean");
		
		HttpEntity<WarehousePropertyDTO> entity = new HttpEntity<>(warehousePropertyMapper.toDto(warehouseProperty), headers);
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehousePropertyPath, entity, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	void putWarehouseProperty() {
		log.info("putWarehouseProperty");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		ResponseEntity<WarehousePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertyPath + "{id}", WarehousePropertyDTO.class, this.warehousePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new WarehouseProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<WarehousePropertyDTO> entity = new HttpEntity<>(warehousePropertyMapper.toDto(dtoUpdate), headers);
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(warehousePropertyPath + this.warehousePropertyTest.getId(), HttpMethod.PUT, entity, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.warehousePropertyTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	void patchWarehouseProperty() {
		log.info("patchWarehouseProperty");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var warehousePropertyPath = this.path + "/warehouseproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(warehousePropertyPath + this.warehousePropertyTest.getId(), HttpMethod.PATCH, entity, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, this.warehousePropertyTest.getId());
		this.warehousePropertyTest = warehousePropertyMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.warehousePropertyTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.warehousePropertyTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	void deleteWarehouseProperty() {
		log.info("deleteWarehouseProperty");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/warehouseproperties/" + this.warehousePropertyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<WarehousePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, this.warehousePropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

