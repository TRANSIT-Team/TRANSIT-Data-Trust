package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.WarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehousePropertyAssemblerWrapper;
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
@Import(WarehousePropertyController.class)
public class WarehousePropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	WarehousePropertyController warehousePropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WarehousePropertyMapper warehousePropertiesMapper;
	@Autowired
	private WarehousePropertyAssemblerWrapper warehousePropertiesAssembler;
	private WarehouseProperty warehousePropertiesTest;
	
	private String path;
	
	private UUID warehouseIdrel;
	
	public WarehousePropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(warehousePropertiesController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(warehousePropertiesController);
		
		
		this.warehousePropertiesTest = setupWarehouseProperty();
		
		
	}
	
	WarehouseProperty setupWarehouseProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehouse = new Warehouse();
		warehouse.setName("TestWarehouse");
		warehouse.setCapacity(12341324l);
		
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		ResponseEntity<WarehouseDTO> response5 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses", warehouse, WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response5.getStatusCode());
		
		var warehouseId = response5.getBody().getId();
		this.warehouseIdrel = warehouseId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/warehouses/" + warehouseId;
		
		var warehouseProperties = new WarehousePropertyDTO();
		warehouseProperties.setKey("Websssssssssseite");
		warehouseProperties.setValue("test-project.com");
		warehouseProperties.setType("text");
		
		
		ResponseEntity<WarehousePropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/warehouseproperties", warehouseProperties, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		return warehousePropertiesMapper.toEntity(response2.getBody());
		
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var warehousePropertiesPath = this.path + "/warehouseproperties/";
		Assertions.assertNotNull(this.warehousePropertiesTest);
		Assertions.assertNotNull(this.warehousePropertiesTest.getId());
		
		Assertions.assertEquals(this.warehousePropertiesTest, warehousePropertiesMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertiesPath + "{id}", WarehousePropertyDTO.class, this.warehousePropertiesTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertiesPath + "{id}", String.class, this.warehousePropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonWarehouseNotNullValues(response);
		
	}
	
	
	void verifyJsonWarehouseNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var warehousePropertiesDTO = warehousePropertiesAssembler.toModel(this.warehousePropertiesTest, this.warehouseIdrel, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.warehousePropertiesTest.getId(), this.warehousePropertiesTest.getCreateDate(), this.warehousePropertiesTest.getModifyDate(), this.warehousePropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.warehousePropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.warehousePropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.warehousePropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + warehousePropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	void getReturnsNotFound() throws Exception {
		var warehousePropertiesPath = this.path + "/warehouseproperties/";
		var warehouseProperties = new WarehousePropertyDTO();
		warehouseProperties.setKey("Web");
		warehouseProperties.setValue("test.com");
		warehouseProperties.setType("text");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/warehouses/" + this.warehouseIdrel + "/warehouseproperties", warehouseProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/warehouses/" + this.warehouseIdrel + "/warehouseproperties", String.class);
		log.info(response.getBody());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("warehouseProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertiesPath + "{id}", WarehousePropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	
	@Test
	void putWarehouseProperties() {
		log.info("putWarehouseProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehousePropertiesPath = this.path + "/warehouseproperties/";
		ResponseEntity<WarehousePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePropertiesPath + "{id}", WarehousePropertyDTO.class, this.warehousePropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new WarehouseProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<WarehousePropertyDTO> entity = new HttpEntity<>(warehousePropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(warehousePropertiesPath + this.warehousePropertiesTest.getId(), HttpMethod.PUT, entity, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.warehousePropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	void patchWarehouseProperties() {
		log.info("patchWarehouseProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var warehousePropertiesPath = this.path + "/warehouseproperties/";
		
		
		String request = "{\"value\":\"test-test.com\"}";
		var entity = new HttpEntity<>(request, headers);
		//patch
		
		var response = template.exchange(warehousePropertiesPath + this.warehousePropertiesTest.getId(), HttpMethod.PATCH, entity, WarehousePropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("test-test.com", response.getBody().getValue());
		ResponseEntity<WarehousePropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, this.warehousePropertiesTest.getId());
		this.warehousePropertiesTest = warehousePropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.warehousePropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.warehousePropertiesTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	void deleteWarehouseProperties() {
		log.info("deleteWarehouse");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/warehouseproperties/" + this.warehousePropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<WarehousePropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseproperties/{id}", WarehousePropertyDTO.class, this.warehousePropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

