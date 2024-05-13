package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.LocationController;
import com.transit.backend.datalayers.controller.assembler.LocationAssembler;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
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
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Import(LocationController.class)
public class LocationControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	LocationController locationController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private LocationMapper locationMapper;
	@Autowired
	private LocationAssembler locationAssembler;
	private Location locationTest;
	
	private String path;
	
	private GeometryFactory geoFactory;
	
	public LocationControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(locationController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(locationController);
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.locationTest = setupLocation(path);
		
		
	}
	
	Location setupLocation(String path) throws JsonProcessingException {
		
		
		var location = new LocationDTO();
		this.geoFactory = new GeometryFactory();
		location.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 20)));
		
		
		ResponseEntity<LocationDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/locations", location, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/locations/{id}", LocationDTO.class, response1.getBody().getId());
		
		return locationMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponseLocation() throws Exception {
		
		var locationPath = path + "/locations/";
		Assertions.assertNotNull(this.locationTest);
		Assertions.assertNotNull(this.locationTest.getId());
		
		Assertions.assertEquals(this.locationTest, locationMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(locationPath + "{id}", LocationDTO.class, this.locationTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(locationPath + "{id}", String.class, this.locationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValuesLocation(path, response);
		
	}
	
	
	void verifyJsonPackageNotNullValuesLocation(String path, final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var locationDTO = locationAssembler.toModel(this.locationTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.locationTest.getId(), this.locationTest.getCreateDate(), this.locationTest.getModifyDate(), this.locationTest.isDeleted());
		
		if (root.get("locationPoint").get("coordinates").isArray()) {
			var coordinates = root.get("locationPoint").withArray("coordinates").elements();
			var x = Double.parseDouble(coordinates.next().toPrettyString());
			var y = Double.parseDouble(coordinates.next().toPrettyString());
			Assertions.assertEquals(this.locationTest.getLocationPoint(), this.geoFactory.createPoint(new Coordinate(x, y)));
		} else {
			Assertions.assertTrue(root.get("locationPoint").get("coordinates").isArray());
		}
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + locationDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	public void getReturnsNotFoundLocation() throws Exception {
		setupLocation(path);
		var locationPath = path + "/locations/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(locationPath, String.class, this.locationTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("locations").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(locationPath + "{id}", LocationDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
		
	}
	
	@Test
	public void postEmptyLocation() {
		log.info("postEmptyLocation");
		var locationPath = path + "/locations/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(locationPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postLocation() {
		log.info("postLocation");
		var locationPath = path + "/locations/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Location location = new Location();
		location.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 30)));
		
		HttpEntity<LocationDTO> entity = new HttpEntity<>(locationMapper.toDto(location), headers);
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(locationPath, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putLocation() throws JsonProcessingException {
		log.info("putLocation");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var locationPath = path + "/locations/";
		ResponseEntity<LocationDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(locationPath + "{id}", LocationDTO.class, this.locationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new Location();
		
		dtoUpdate.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 40)));
		dto.setLocationPoint(dtoUpdate.getLocationPoint());
		
		HttpEntity<LocationDTO> entity = new HttpEntity<>(locationMapper.toDto(dtoUpdate), headers);
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(locationPath + this.locationTest.getId(), HttpMethod.PUT, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.locationTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getLocationPoint(), response2.getBody().getLocationPoint());
	}
	
	@Test
	public void patchLocation() throws JsonProcessingException {
		log.info("patchLocation");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var locationPath = path + "/locations/";
		
		
		String request = "{  \"locationPoint\": { \"type\": \"Point\", \"coordinates\": [ 50, 12 ] }}";
		var entity = new HttpEntity<String>(request, headers);
		//patch
		
		var response = template.exchange(locationPath + this.locationTest.getId(), HttpMethod.PATCH, entity, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<LocationDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/locations/{id}", LocationDTO.class, this.locationTest.getId());
		this.locationTest = locationMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.locationTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.locationTest.getLocationPoint(), response.getBody().getLocationPoint());
	}
	
	@Test
	public void deleteLocation() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/locations/" + this.locationTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<LocationDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/locations/{id}", LocationDTO.class, this.locationTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(Objects.requireNonNull(response3.getBody()).isDeleted());
	}
	
	
}
