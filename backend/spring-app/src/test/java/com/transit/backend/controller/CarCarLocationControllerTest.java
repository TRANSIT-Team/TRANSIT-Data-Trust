package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CarCarLocationController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarLocationAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.UUID;

@Slf4j
@Import(CarCarLocationController.class)
public class CarCarLocationControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CarCarLocationController carLocationComtroller;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private LocationMapper carLocationMapper;
	@Autowired
	private CarCarLocationAssemblerWrapper carLocation;
	private Location carLocationTest;
	
	private String path;
	
	private UUID carId;
	
	private GeometryFactory geoFactory;
	
	public CarCarLocationControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(carLocationComtroller);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(carLocationComtroller);
		
		
		this.carLocationTest = setupLocation();
		
		
	}
	
	Location setupLocation() throws JsonProcessingException {
		this.geoFactory = new GeometryFactory();
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
		log.info("CarId");
		
		this.carId = carId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/cars/" + carId;
		
		var carLocation = new LocationDTO();
		carLocation.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		
		ResponseEntity<LocationDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/carlocations", carLocation, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		log.info(response1.getBody().toString());
		log.info(carLocationMapper.toEntity(response1.getBody()).getId().toString());
		
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carlocations/{id}", LocationDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		log.info(carLocationMapper.toEntity(response2.getBody()).getId().toString());
		return carLocationMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var carLocationPath = this.path + "/carlocations/";
		log.info(this.carLocationTest.getId().toString());
		Assertions.assertNotNull(this.carLocationTest);
		Assertions.assertNotNull(this.carLocationTest.getId());
		
		Assertions.assertEquals(this.carLocationTest, carLocationMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(carLocationPath + "{id}", LocationDTO.class, this.carLocationTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carLocationPath + "{id}", String.class, this.carLocationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var carLocationDTO = carLocation.toModel(this.carLocationTest, this.carId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.carLocationTest.getId(), this.carLocationTest.getCreateDate(), this.carLocationTest.getModifyDate(), this.carLocationTest.isDeleted());
		if (root.get("locationPoint").get("coordinates").isArray()) {
			var coordinates = root.get("locationPoint").withArray("coordinates").elements();
			var x = Double.parseDouble(coordinates.next().toPrettyString());
			var y = Double.parseDouble(coordinates.next().toPrettyString());
			Assertions.assertEquals(this.carLocationTest.getLocationPoint(), this.geoFactory.createPoint(new Coordinate(x, y)));
		} else {
			Assertions.assertTrue(root.get("locationPoint").get("coordinates").isArray());
		}
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + carLocationDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	void getReturnsNotFound() throws Exception {
		var carLocationPath = this.path + "/carlocations/";
		var carLocation = new LocationDTO();
		carLocation.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 5.233)));
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/carlocations", carLocation, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carlocations", String.class);
		log.info(response.getBody());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("locations").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		log.error(String.valueOf(selected.size()));
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carLocationPath + "{id}", LocationDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	void postEmptyLocation() {
		log.info("postEmptyLocation");
		var carLocationPath = this.path + "/carlocations/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carLocationPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	void postLocation() {
		log.info("postLocationNotFound");
		var carLocationPath = this.path + "/carlocations/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		LocationDTO carLocation = new LocationDTO();
		carLocation.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		HttpEntity<LocationDTO> entity = new HttpEntity<>(carLocation, headers);
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carLocationPath, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	void putLocation() {
		log.info("putLocation");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var carLocationPath = this.path + "/carlocations/";
		ResponseEntity<LocationDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carLocationPath + "{id}", LocationDTO.class, this.carLocationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var updateEntity = this.carLocationMapper.toEntity(dto);
		updateEntity.setCreateDate(null);
		updateEntity.setDeleted(false);
		updateEntity.setModifyDate(null);
		updateEntity.setCreatedBy(null);
		updateEntity.setLastModifiedBy(null);
		//updateEntity.setLocation(this.carLocationMapper.toEntity(dto));
		//updateEntity.setCar(this.carLocationMapper.toEntity(dto).getCar());
		//updateEntity.setId(this.carLocationMapper.toEntity(dto).getId());
		updateEntity.setLocationPoint(geoFactory.createPoint(new Coordinate(23.4234314, 23434.32142343)));
		dto.setLocationPoint(updateEntity.getLocationPoint());
		
		
		updateEntity.setId(dto.getId());
		HttpEntity<LocationDTO> entity = new HttpEntity<>(carLocationMapper.toDto(updateEntity), headers);
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(carLocationPath + this.carLocationTest.getId(), HttpMethod.PUT, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.carLocationTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getLocationPoint(), response2.getBody().getLocationPoint());
	}
	
	@Test
	void patchLocation() {
		log.info("patchLocation");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var carLocationPath = this.path + "/carlocations/";
		
		var points = "12.34,15.23";
		
		var request = "{\"locationPoint\": {\"type\": \"Point\",\"coordinates\": [" + points + "]}}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(carLocationPath + this.carLocationTest.getId(), HttpMethod.PATCH, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(geoFactory.createPoint(new Coordinate(Double.parseDouble(points.split(",")[0]), Double.parseDouble(points.split(",")[1]))), response.getBody().getLocationPoint());
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carlocations/{id}", LocationDTO.class, this.carLocationTest.getId());
		this.carLocationTest = carLocationMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.carLocationTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.carLocationTest.getLocationPoint(), response.getBody().getLocationPoint());
	}
	
	@Test
	void deleteLocation() {
		log.info("deleteCar");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/carlocations/" + this.carLocationTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<LocationDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/carlocations/{id}", LocationDTO.class, this.carLocationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
}

