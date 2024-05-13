package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.WarehouseAddressController;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehouseAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
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

import java.util.UUID;

@Slf4j
@Import(WarehouseAddressController.class)
public class WarehouseAddressControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	WarehouseAddressController warehouseAddressComtroller;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private AddressMapper warehouseAddressMapper;
	@Autowired
	private WarehouseWarehouseAddressAssemblerWrapper warehouseAddress;
	private Address warehouseAddressTest;
	
	private String path;
	
	private UUID warehouseId;
	
	private GeometryFactory geoFactory;
	
	public WarehouseAddressControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(warehouseAddressComtroller);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(warehouseAddressComtroller);
		
		
		this.warehouseAddressTest = setupAddress();
		
		
	}
	
	Address setupAddress() throws JsonProcessingException {
		this.geoFactory = new GeometryFactory();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehouse = new Warehouse();
		warehouse.setName("warehouse");
		warehouse.setCapacity(12341324l);
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		ResponseEntity<WarehouseDTO> response5 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses", warehouse, WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response5.getStatusCode());
		
		var warehouseId = response5.getBody().getId();
		log.info("WarehouseId");
		
		this.warehouseId = warehouseId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/warehouses/" + warehouseId;
		
		var address = new AddressDTO();
		address.setLocationPoint(geoFactory.createPoint(new Coordinate(50.436, 20)));
		address.setCity("TestStadt");
		address.setState("TestState");
		address.setCountry("TestLand");
		address.setStreet("Teststrasse");
		address.setIsoCode("DE");
		address.setZip("11111");
		address.setAddressExtra("50. Etage");
		
		ResponseEntity<AddressDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/warehouseaddresses", address, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		log.info(response1.getBody().toString());
		log.info(warehouseAddressMapper.toEntity(response1.getBody()).getId().toString());
		
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseaddresses/{id}", AddressDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		log.info(warehouseAddressMapper.toEntity(response2.getBody()).getId().toString());
		return warehouseAddressMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var warehouseAddressPath = this.path + "/warehouseaddresses/";
		log.info(this.warehouseAddressTest.getId().toString());
		Assertions.assertNotNull(this.warehouseAddressTest);
		Assertions.assertNotNull(this.warehouseAddressTest.getId());
		
		Assertions.assertEquals(this.warehouseAddressTest, warehouseAddressMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehouseAddressPath + "{id}", AddressDTO.class, this.warehouseAddressTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehouseAddressPath + "{id}", String.class, this.warehouseAddressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonWarehouseAddressNotNullValues(response);
		
	}
	
	
	void verifyJsonWarehouseAddressNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var warehouseAddressDTO = warehouseAddress.toModel(this.warehouseAddressTest, this.warehouseId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.warehouseAddressTest.getId(), this.warehouseAddressTest.getCreateDate(), this.warehouseAddressTest.getModifyDate(), this.warehouseAddressTest.isDeleted());
		if (root.get("locationPoint").get("coordinates").isArray()) {
			var coordinates = root.get("locationPoint").withArray("coordinates").elements();
			var x = Double.parseDouble(coordinates.next().toPrettyString());
			var y = Double.parseDouble(coordinates.next().toPrettyString());
			Assertions.assertEquals(this.warehouseAddressTest.getLocationPoint(), this.geoFactory.createPoint(new Coordinate(x, y)));
		} else {
			Assertions.assertTrue(root.get("locationPoint").get("coordinates").isArray());
		}
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + warehouseAddressDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	void postEmptyAddress() {
		log.info("postEmptyAddress");
		var warehouseAddressPath = this.path + "/warehouseaddresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehouseAddressPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	void postAddress() {
		log.info("postAddressNotFound");
		var warehouseAddressPath = this.path + "/warehouseaddresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		AddressDTO warehouseAddress = new AddressDTO();
		warehouseAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		warehouseAddress.setCity("TestStadt");
		warehouseAddress.setState("TestState");
		warehouseAddress.setCountry("TestLand");
		warehouseAddress.setStreet("Teststrasse");
		warehouseAddress.setIsoCode("DE");
		warehouseAddress.setZip("11111");
		warehouseAddress.setAddressExtra("50. Etage");
		
		HttpEntity<AddressDTO> entity = new HttpEntity<>(warehouseAddress, headers);
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehouseAddressPath, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response2.getStatusCode());
	}
	
	@Test
	void putAddress() {
		log.info("putAddress");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehouseAddressPath = this.path + "/warehouseaddresses/";
		ResponseEntity<AddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehouseAddressPath + "{id}", AddressDTO.class, this.warehouseAddressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var updateEntity = this.warehouseAddressMapper.toEntity(dto);
		updateEntity.setCreateDate(null);
		updateEntity.setDeleted(false);
		updateEntity.setModifyDate(null);
		updateEntity.setCreatedBy(null);
		updateEntity.setLastModifiedBy(null);
		//updateEntity.setAddress(this.warehouseAddressMapper.toEntity(dto));
		//updateEntity.setWarehouse(this.warehouseAddressMapper.toEntity(dto).getWarehouse());
		//updateEntity.setId(this.warehouseAddressMapper.toEntity(dto).getId());
		updateEntity.setLocationPoint(geoFactory.createPoint(new Coordinate(23.4234314, 23434.32142343)));
		dto.setLocationPoint(updateEntity.getLocationPoint());
		
		
		updateEntity.setId(dto.getId());
		HttpEntity<AddressDTO> entity = new HttpEntity<>(warehouseAddressMapper.toDto(updateEntity), headers);
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(warehouseAddressPath + this.warehouseAddressTest.getId(), HttpMethod.PUT, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.warehouseAddressTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getLocationPoint(), response2.getBody().getLocationPoint());
	}
	
	@Test
	void patchAddress() {
		log.info("patchAddressAddress");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var warehouseAddressPath = this.path + "/warehouseaddresses/";
		
		var points = "12.34, 15.23";
		
		var request = "{\"locationPoint\": {\"type\": \"Point\",\"coordinates\": [" + points + "]}}";
		log.info(request);
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(warehouseAddressPath + this.warehouseAddressTest.getId(), HttpMethod.PATCH, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals(geoFactory.createPoint(new Coordinate(Double.parseDouble(points.split(",")[0]), Double.parseDouble(points.split(",")[1]))), response.getBody().getLocationPoint());
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseaddresses/{id}", AddressDTO.class, this.warehouseAddressTest.getId());
		this.warehouseAddressTest = warehouseAddressMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.warehouseAddressTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.warehouseAddressTest.getLocationPoint(), response.getBody().getLocationPoint());
	}
	
	@Test
	void deleteAddress() {
		log.info("deleteWarehouseAddress");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/warehouseaddresses/" + this.warehouseAddressTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<AddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/warehouseaddresses/{id}", AddressDTO.class, this.warehouseAddressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
}

