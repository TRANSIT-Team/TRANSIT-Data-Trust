package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.AddressController;
import com.transit.backend.datalayers.controller.assembler.AddressAssembler;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.service.impl.UserServiceBean;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@Import(AddressController.class)
public class AddressControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	AddressController addressController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private AddressAssembler addressAssembler;
	private Address addressTest;
	
	private String path;
	
	private GeometryFactory geoFactory;
	
	private KeycloakServiceManager keycloakServiceManager;
	
	public AddressControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		this.keycloakServiceManager = keycloakServiceManager;
		
	}
	
	@Test
	public void contextLoads() {
		var uS = Mockito.spy(new UserServiceBean(keycloakServiceManager));
		log.info("mock email");
		when(uS.executeActionMails(any(), any())).thenReturn(true);
		Assertions.assertNotNull(addressController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		
		super.updateRestTemplate(false);
		log.info("serverPort");
		log.info(getRandomServerPort().toString());
		log.info(getDefaultCompany().getId().toString());
		Assertions.assertNotNull(addressController);
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.addressTest = setupAddress(path);
		
		
	}
	
	Address setupAddress(String path) throws JsonProcessingException {
		
		
		var address = new AddressDTO();
		this.geoFactory = new GeometryFactory();
		address.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 20)));
		address.setCity("TestStadt");
		address.setState("TestState");
		address.setCountry("TestLand");
		address.setStreet("Teststrasse");
		address.setIsoCode("DE");
		address.setZip("11111");
		address.setAddressExtra("50. Etage");
		log.info("request");
		ResponseEntity<AddressDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", address, AddressDTO.class);
		
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/addresses/{id}", AddressDTO.class, response1.getBody().getId());
		
		return addressMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponseAddress() throws Exception {
		
		var addressPath = path + "/addresses/";
		Assertions.assertNotNull(this.addressTest);
		Assertions.assertNotNull(this.addressTest.getId());
		
		Assertions.assertEquals(this.addressTest, addressMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(addressPath + "{id}", AddressDTO.class, this.addressTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(addressPath + "{id}", String.class, this.addressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValuesAddress(path, response);
		
	}
	
	
	void verifyJsonPackageNotNullValuesAddress(String path, final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var addressDTO = addressAssembler.toModel(this.addressTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.addressTest.getId(), this.addressTest.getCreateDate(), this.addressTest.getModifyDate(), this.addressTest.isDeleted());
		
		
		Assertions.assertEquals(this.addressTest.getCity(), root.get("city").asText());
		Assertions.assertEquals(this.addressTest.getState(), root.get("state").asText());
		Assertions.assertEquals(this.addressTest.getCountry(), root.get("country").asText());
		Assertions.assertEquals(this.addressTest.getStreet(), root.get("street").asText());
		Assertions.assertEquals(this.addressTest.getIsoCode(), root.get("isoCode").asText());
		Assertions.assertEquals(this.addressTest.getZip(), root.get("zip").asText());
		Assertions.assertEquals(this.addressTest.getAddressExtra(), root.get("addressExtra").asText());
		if (root.get("locationPoint").get("coordinates").isArray()) {
			var coordinates = root.get("locationPoint").withArray("coordinates").elements();
			var x = Double.parseDouble(coordinates.next().toPrettyString());
			var y = Double.parseDouble(coordinates.next().toPrettyString());
			Assertions.assertEquals(this.addressTest.getLocationPoint(), this.geoFactory.createPoint(new Coordinate(x, y)));
		} else {
			Assertions.assertTrue(root.get("locationPoint").get("coordinates").isArray());
		}
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + addressDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	public void getReturnsNotFoundAddress() throws Exception {
		setupAddress(path);
		var addressPath = path + "/addresses/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(addressPath, String.class, this.addressTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("addresses").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(addressPath + "{id}", AddressDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
		
	}
	
	@Test
	public void postEmptyAddress() {
		var x = TransitAuthorities.values()[0];
		log.info("postEmptyAddress");
		var addressPath = path + "/addresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(addressPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postAddress() {
		log.info("postAddressNotFound");
		var addressPath = path + "/addresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Address address = new Address();
		address.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 30)));
		address.setCity("TestStadt");
		address.setState("TestState");
		address.setCountry("TestLand");
		address.setStreet("Teststrasse");
		address.setIsoCode("DE");
		address.setZip("11111");
		address.setAddressExtra("50. Etage");
		
		HttpEntity<AddressDTO> entity = new HttpEntity<>(addressMapper.toDto(address), headers);
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(addressPath, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putAddress() throws JsonProcessingException {
		log.info("putAddress");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var addressPath = path + "/addresses/";
		ResponseEntity<AddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(addressPath + "{id}", AddressDTO.class, this.addressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var update = new Address();
		// addressMapper.toEntity(dto);
		
		update.setState("testTestTestState");
		dto.setState(update.getState());
		
		HttpEntity<AddressDTO> entity = new HttpEntity<>(addressMapper.toDto(update), headers);
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(addressPath + this.addressTest.getId(), HttpMethod.PUT, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response2.getStatusCode());
		update = addressMapper.toEntity(dto);
		update.setId(null);
		update.setState("testTestTestState");
		update.setCreateDate(null);
		update.setModifyDate(null);
		update.setCreatedBy(null);
		update.setLastModifiedBy(null);
		
		dto.setState(update.getState());
		entity = new HttpEntity<>(addressMapper.toDto(update), headers);
		response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(addressPath + this.addressTest.getId(), HttpMethod.PUT, entity, AddressDTO.class);
		
		
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.addressTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getState(), response2.getBody().getState());
		Assertions.assertNotNull(response2.getBody().getCreatedBy());
	}
	
	@Test
	public void patchAddress() throws JsonProcessingException {
		log.info("patchAddress");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var addressPath = path + "/addresses/";
		
		
		String request = "{\"country\":\"TestTestCountry\"}";
		var entity = new HttpEntity<String>(request, headers);
		//patch
		
		var response = template.exchange(addressPath + this.addressTest.getId(), HttpMethod.PATCH, entity, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<AddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/addresses/{id}", AddressDTO.class, this.addressTest.getId());
		this.addressTest = addressMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.addressTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.addressTest.getCountry(), response.getBody().getCountry());
	}
	
	@Test
	public void deleteAddress() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/addresses/" + this.addressTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<AddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/addresses/{id}", AddressDTO.class, this.addressTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(Objects.requireNonNull(response3.getBody()).isDeleted());
	}
	
	
}
