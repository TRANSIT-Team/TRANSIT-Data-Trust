package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CompanyCompanyAddressController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyAddressId;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
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
@Import(CompanyCompanyAddressController.class)
public class CompanyCompanyAddressControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CompanyCompanyAddressController companyAddressComtroller;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyAddressMapper companyAddressMapper;
	@Autowired
	private CompanyCompanyAddressAssemblerWrapper companyAddress;
	private CompanyAddress companyAddressTest;
	
	private String path;
	
	private UUID companyId;
	
	private GeometryFactory geoFactory;
	
	public CompanyCompanyAddressControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(companyAddressComtroller);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(companyAddressComtroller);
		
		
		this.companyAddressTest = setupCompanyAddress();
		
		
	}
	
	CompanyAddress setupCompanyAddress() throws JsonProcessingException {
		this.geoFactory = new GeometryFactory();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var companyId = super.getDefaultCompany().getId();
		log.info("CompanyId");
		
		this.companyId = companyId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + companyId;
		
		var companyAddress = new CompanyAddressDTO();
		companyAddress.setCity("TestStadt");
		companyAddress.setState("TestState");
		companyAddress.setCountry("TestLand");
		companyAddress.setStreet("Teststrasse");
		companyAddress.setIsoCode("DE");
		companyAddress.setZip("11111");
		companyAddress.setAddressExtra("50. Etage");
		companyAddress.setAddressType("Hauptadresse");
		companyAddress.setCompanyId(this.companyId);
		companyAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		
		ResponseEntity<CompanyAddressDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/companyaddresses", companyAddress, CompanyAddressDTO.class);
		log.info(response1.getBody().getCompanyId().toString());
		log.info(companyAddressMapper.toEntity(response1.getBody()).getId().getCompanyId().toString());
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<CompanyAddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyaddresses/{id}", CompanyAddressDTO.class, response1.getBody().getId());
		log.info(companyAddressMapper.toEntity(response2.getBody()).getId().getCompanyId().toString());
		return companyAddressMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var companyAddressPath = this.path + "/companyaddresses/";
		log.info(this.companyAddressTest.getId().getCompanyId().toString());
		Assertions.assertNotNull(this.companyAddressTest);
		Assertions.assertNotNull(this.companyAddressTest.getId());
		
		Assertions.assertEquals(this.companyAddressTest, companyAddressMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyAddressPath + "{id}", CompanyAddressDTO.class, this.companyAddressTest.getId().getAddressId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyAddressPath + "{id}", String.class, this.companyAddressTest.getId().getAddressId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var companyAddressDTO = companyAddress.toModel(this.companyAddressTest, this.companyId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.companyAddressTest.getAddress().getId(), this.companyAddressTest.getCreateDate(), this.companyAddressTest.getModifyDate(), this.companyAddressTest.isDeleted());
		Assertions.assertEquals(this.companyAddressTest.getCompany().getId(), UUID.fromString(root.get("companyId").asText()));
		
		Assertions.assertEquals(this.companyAddressTest.getAddress().getCity(), root.get("city").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getState(), root.get("state").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getCountry(), root.get("country").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getStreet(), root.get("street").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getIsoCode(), root.get("isoCode").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getZip(), root.get("zip").asText());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getAddressExtra(), root.get("addressExtra").asText());
		if (root.get("locationPoint").get("coordinates").isArray()) {
			var coordinates = root.get("locationPoint").withArray("coordinates").elements();
			var x = Double.parseDouble(coordinates.next().toPrettyString());
			var y = Double.parseDouble(coordinates.next().toPrettyString());
			Assertions.assertEquals(this.companyAddressTest.getAddress().getLocationPoint(), this.geoFactory.createPoint(new Coordinate(x, y)));
		} else {
			Assertions.assertTrue(root.get("locationPoint").get("coordinates").isArray());
		}
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + companyAddressDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var companyAddressPath = this.path + "/companyaddresses/";
		var companyAddress = new CompanyAddressDTO();
		companyAddress.setCity("TestStadt");
		companyAddress.setState("TestState");
		companyAddress.setCountry("TestLand");
		companyAddress.setStreet("Teststrasse");
		companyAddress.setIsoCode("DE");
		companyAddress.setZip("11111");
		companyAddress.setAddressExtra("300. Etage");
		companyAddress.setAddressType("Hauptanschrift");
		companyAddress.setCompanyId(this.companyId);
		companyAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/companyaddresses", companyAddress, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyaddresses", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("companyAddresses").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CompanyAddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyAddressPath + "{id}", CompanyAddressDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	public void postEmptyCompanyAddress() {
		log.info("postEmptyCompanyAddress");
		var companyAddressPath = this.path + "/companyaddresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyAddressPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postCompanyAddress() {
		log.info("postCompanyAddressNotFound");
		var companyAddressPath = this.path + "/companyaddresses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		CompanyAddressDTO companyAddress = new CompanyAddressDTO();
		companyAddress.setCity("TestStadt");
		companyAddress.setState("TestState");
		companyAddress.setCountry("TestLand");
		companyAddress.setStreet("Teststrasse");
		companyAddress.setIsoCode("DE");
		companyAddress.setZip("11111");
		companyAddress.setAddressExtra("300. Etage");
		companyAddress.setAddressType("Hauptanschrift");
		companyAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50, 50)));
		companyAddress.setCompanyId(this.companyId);
		HttpEntity<CompanyAddressDTO> entity = new HttpEntity<>(companyAddress, headers);
		ResponseEntity<CompanyAddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyAddressPath, entity, CompanyAddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putCompanyAddress() {
		log.info("putCompanyAddress");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var companyAddressPath = this.path + "/companyaddresses/";
		ResponseEntity<CompanyAddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyAddressPath + "{id}", CompanyAddressDTO.class, this.companyAddressTest.getId().getAddressId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var updateEntity = this.companyAddressMapper.toEntity(dto);
		updateEntity.setCreateDate(null);
		updateEntity.setDeleted(false);
		updateEntity.setModifyDate(null);
		updateEntity.setCreatedBy(null);
		updateEntity.setLastModifiedBy(null);
		//updateEntity.setAddress(this.companyAddressMapper.toEntity(dto).getAddress());
		//updateEntity.setCompany(this.companyAddressMapper.toEntity(dto).getCompany());
		//updateEntity.setId(this.companyAddressMapper.toEntity(dto).getId());
		updateEntity.setAddressType("TestAnschrift");
		dto.setAddressType(updateEntity.getAddressType());
		
		
		updateEntity.setId(new CompanyAddressId(dto.getId(), dto.getCompanyId()));
		HttpEntity<CompanyAddressDTO> entity = new HttpEntity<>(companyAddressMapper.toDto(updateEntity), headers);
		ResponseEntity<CompanyAddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(companyAddressPath + this.companyAddressTest.getId().getAddressId(), HttpMethod.PUT, entity, CompanyAddressDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.companyAddressTest.getId().getAddressId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getAddressType(), response2.getBody().getAddressType());
	}
	
	@Test
	public void patchCompanyAddress() {
		log.info("patchCompanyAddress");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var companyAddressPath = this.path + "/companyaddresses/";
		
		
		String request = "{\"city\":\"TestTestStadt\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(companyAddressPath + this.companyAddressTest.getId().getAddressId(), HttpMethod.PATCH, entity, CompanyAddressDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("TestTestStadt", response.getBody().getCity());
		ResponseEntity<CompanyAddressDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyaddresses/{id}", CompanyAddressDTO.class, this.companyAddressTest.getId().getAddressId());
		this.companyAddressTest = companyAddressMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.companyAddressTest.getId().getAddressId(), response.getBody().getId());
		Assertions.assertEquals(this.companyAddressTest.getAddress().getCity(), response.getBody().getCity());
	}
	
	@Test
	public void deleteCompanyAddress() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/companyaddresses/" + this.companyAddressTest.getId().getAddressId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CompanyAddressDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyaddresses/{id}", CompanyAddressDTO.class, this.companyAddressTest.getId().getAddressId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
}

