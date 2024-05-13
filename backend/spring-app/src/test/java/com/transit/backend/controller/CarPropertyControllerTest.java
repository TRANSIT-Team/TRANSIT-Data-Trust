package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CarPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarPropertyAssemblerWrapper;
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
@Import(CarPropertyController.class)
public class CarPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CarPropertyController carPropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CarPropertyMapper carPropertiesMapper;
	@Autowired
	private CarPropertyAssemblerWrapper carPropertiesAssembler;
	private CarProperty carPropertiesTest;
	
	private String path;
	
	private UUID carIdrel;
	
	public CarPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(carPropertiesController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(carPropertiesController);
		
		
		this.carPropertiesTest = setupCarProperty();
		
		
	}
	
	CarProperty setupCarProperty() throws JsonProcessingException {
		
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
		this.carIdrel = carId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/cars/" + carId;
		
		var carProperties = new CarPropertyDTO();
		carProperties.setKey("Websssssssssseite");
		carProperties.setValue("test-project.com");
		carProperties.setType("text");
		
		
		ResponseEntity<CarPropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/carproperties", carProperties, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		return carPropertiesMapper.toEntity(response2.getBody());
		
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var carPropertiesPath = this.path + "/carproperties/";
		Assertions.assertNotNull(this.carPropertiesTest);
		Assertions.assertNotNull(this.carPropertiesTest.getId());
		
		Assertions.assertEquals(this.carPropertiesTest, carPropertiesMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertiesPath + "{id}", CarPropertyDTO.class, this.carPropertiesTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertiesPath + "{id}", String.class, this.carPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonCarNotNullValues(response);
		
	}
	
	
	void verifyJsonCarNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var carPropertiesDTO = carPropertiesAssembler.toModel(this.carPropertiesTest, this.carIdrel, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.carPropertiesTest.getId(), this.carPropertiesTest.getCreateDate(), this.carPropertiesTest.getModifyDate(), this.carPropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.carPropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.carPropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.carPropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + carPropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	void getReturnsNotFound() throws Exception {
		var carPropertiesPath = this.path + "/carproperties/";
		var carProperties = new CarPropertyDTO();
		carProperties.setKey("Web");
		carProperties.setValue("test.com");
		carProperties.setType("text");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/cars/" + this.carIdrel + "/carproperties", carProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/cars/" + this.carIdrel + "/carproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("carProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertiesPath + "{id}", CarPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	
	@Test
	void putCarProperties() {
		log.info("putCarProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var carPropertiesPath = this.path + "/carproperties/";
		ResponseEntity<CarPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPropertiesPath + "{id}", CarPropertyDTO.class, this.carPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new CarProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<CarPropertyDTO> entity = new HttpEntity<>(carPropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(carPropertiesPath + this.carPropertiesTest.getId(), HttpMethod.PUT, entity, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.carPropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	void patchCarProperties() {
		log.info("patchCarProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var carPropertiesPath = this.path + "/carproperties/";
		
		
		String request = "{\"value\":\"test-test.com\"}";
		var entity = new HttpEntity<>(request, headers);
		//patch
		
		var response = template.exchange(carPropertiesPath + this.carPropertiesTest.getId(), HttpMethod.PATCH, entity, CarPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("test-test.com", response.getBody().getValue());
		ResponseEntity<CarPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, this.carPropertiesTest.getId());
		this.carPropertiesTest = carPropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.carPropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.carPropertiesTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	void deleteCarProperties() {
		log.info("deleteCar");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/carproperties/" + this.carPropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CarPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carproperties/{id}", CarPropertyDTO.class, this.carPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

