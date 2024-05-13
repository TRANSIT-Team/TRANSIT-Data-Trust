package com.transit.backend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CarCarPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
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
@Import(CarCarPropertyController.class)
public class CarCarPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CarCarPropertyController carPropertyController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	@Autowired
	private CarCarPropertyAssemblerWrapper carPropertyAssembler;
	private CarProperty carPropertyTest;
	
	private String path;
	
	private UUID carId;
	
	public CarCarPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(carPropertyController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(carPropertyController);
		
		
		this.carPropertyTest = setupPackageProperty();
		
		
	}
	
	CarProperty setupPackageProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var car = new Car();
		car.setType("VAN");
		car.setCapacity("12341324");
		car.setPlate("4234");
		car.setWeight("32532532");
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		ResponseEntity<CarDTO> response5 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars", car, CarDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response5.getStatusCode());
		
		var carId = response5.getBody().getId();
		this.carId = carId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/cars/" + carId;
		
		var carProperty = new CarPropertyDTO();
		carProperty.setKey("Webpage");
		carProperty.setValue("test-firma.org");
		carProperty.setType("text");
		
		
		ResponseEntity<CarPropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/carproperties", carProperty, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		return carPropertyMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var carPropertyPath = this.path + "/carproperties/";
		Assertions.assertNotNull(this.carPropertyTest);
		Assertions.assertNotNull(this.carPropertyTest.getId());
		
		Assertions.assertEquals(this.carPropertyTest, carPropertyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertyPath + "{id}", CarPropertyDTO.class, this.carPropertyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertyPath + "{id}", String.class, this.carPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var carPropertyDTO = carPropertyAssembler.toModel(this.carPropertyTest, this.carId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.carPropertyTest.getId(), this.carPropertyTest.getCreateDate(), this.carPropertyTest.getModifyDate(), this.carPropertyTest.isDeleted());
		
		Assertions.assertEquals(this.carPropertyTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.carPropertyTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.carPropertyTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + carPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	void getReturnsNotFound() throws Exception {
		var carPropertyPath = this.path + "/carproperties/";
		var carProperty = new CarPropertyDTO();
		carProperty.setKey("testeigenschaft2");
		carProperty.setValue("F");
		carProperty.setType("string");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/carproperties", carProperty, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("carProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertyPath + "{id}", CarPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	void postEmptyCarProperty() {
		log.info("postEmptyCarProperty");
		var carPropertyPath = this.path + "/carproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carPropertyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	void postCarProperty() {
		log.info("postCarPropertyNotFound");
		var carPropertyPath = this.path + "/carproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		CarProperty carProperty = new CarProperty();
		carProperty.setKey("testeigenschaft3");
		carProperty.setValue("true");
		carProperty.setType("boolean");
		
		HttpEntity<CarPropertyDTO> entity = new HttpEntity<>(carPropertyMapper.toDto(carProperty), headers);
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carPropertyPath, entity, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	void putCarProperty() {
		log.info("putCarProperty");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var carPropertyPath = this.path + "/carproperties/";
		ResponseEntity<CarPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertyPath + "{id}", CarPropertyDTO.class, this.carPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new CarProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<CarPropertyDTO> entity = new HttpEntity<>(carPropertyMapper.toDto(dtoUpdate), headers);
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(carPropertyPath + this.carPropertyTest.getId(), HttpMethod.PUT, entity, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.carPropertyTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	void patchCarProperty() {
		log.info("patchCarProperty");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var carPropertyPath = this.path + "/carproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(carPropertyPath + this.carPropertyTest.getId(), HttpMethod.PATCH, entity, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, this.carPropertyTest.getId());
		this.carPropertyTest = carPropertyMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.carPropertyTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.carPropertyTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	void deleteCarProperty() {
		log.info("deleteCarProperty");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/carproperties/" + this.carPropertyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CarPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, this.carPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

