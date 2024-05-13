package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CompanyDeliveryAreaController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyDeliveryAreaAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.service.mapper.CompanyDeliveryAreaMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Import(CompanyDeliveryAreaController.class)
public class CompanyDeliveryAreaControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CompanyDeliveryAreaController companyDeliveryAreaComtroller;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyDeliveryAreaMapper companyDeliveryAreaMapper;
	@Autowired
	private CompanyCompanyDeliveryAreaAssemblerWrapper companyDeliveryArea;
	private CompanyDeliveryArea companyDeliveryAreaTest;
	
	private String path;
	
	private UUID companyId;
	
	private GeometryFactory geoFactory;
	
	public CompanyDeliveryAreaControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(companyDeliveryAreaComtroller);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(companyDeliveryAreaComtroller);
		
		
		this.companyDeliveryAreaTest = setupCompanyDeliveryArea();
		
		
	}
	
	CompanyDeliveryArea setupCompanyDeliveryArea() throws JsonProcessingException {
		this.geoFactory = new GeometryFactory();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var company = super.getDefaultCompany();
		
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		
		var companyId = company.getId();
		log.info("CompanyId");
		
		this.companyId = companyId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + companyId;
		
		ResponseEntity<CompanyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path, CompanyDTO.class);
		ResponseEntity<String> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path, String.class);
		log.info(response3.getBody());
		JsonNode root = objectMapper.readTree(response3.getBody());
		
		var deliveryAreaLink = root.get("_links").get("companyDeliveryArea").get("href");
		
		log.info(deliveryAreaLink.asText());
		var deliveryArea = new CompanyDeliveryAreaDTO();
		deliveryArea.setDeliveryAreaZips(new ArrayList<>(Arrays.asList(new String[]{"00000", "00001"})));
		deliveryArea.setCompanyId(this.companyId);
		
		ResponseEntity<CompanyDeliveryAreaDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(deliveryAreaLink.asText(), CompanyDeliveryAreaDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		log.info(response1.getBody().toString());
		log.info(companyDeliveryAreaMapper.toEntity(response2.getBody()).getId().toString());
		
		//ResponseEntity<CompanyDeliveryAreaDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/deliveryarea/{id}", CompanyDeliveryAreaDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		log.info(companyDeliveryAreaMapper.toEntity(response2.getBody()).getId().toString());
		return companyDeliveryAreaMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponse() throws Exception {
		
		var companyDeliveryAreaPath = this.path + "/deliveryarea/";
		log.info(this.companyDeliveryAreaTest.getId().toString());
		Assertions.assertNotNull(this.companyDeliveryAreaTest);
		Assertions.assertNotNull(this.companyDeliveryAreaTest.getId());
		
		Assertions.assertEquals(this.companyDeliveryAreaTest, companyDeliveryAreaMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyDeliveryAreaPath + "{id}", CompanyDeliveryAreaDTO.class, this.companyDeliveryAreaTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyDeliveryAreaPath + "{id}", String.class, this.companyDeliveryAreaTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonCompanyDeliveryAreaNotNullValues(response);
		
	}
	
	
	void verifyJsonCompanyDeliveryAreaNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var companyDeliveryAreaDTO = companyDeliveryArea.toModel(this.companyDeliveryAreaTest, this.companyId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.companyDeliveryAreaTest.getId(), this.companyDeliveryAreaTest.getCreateDate(), this.companyDeliveryAreaTest.getModifyDate(), this.companyDeliveryAreaTest.isDeleted());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + companyDeliveryAreaDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	void postEmptyCompanyDeliveryArea() {
		log.info("postEmptyCompanyDeliveryArea");
		var companyDeliveryAreaPath = this.path + "/deliveryarea/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyDeliveryAreaPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	void postCompanyDeliveryArea() {
		log.info("postCompanyDeliveryAreaNotFound");
		var companyDeliveryAreaPath = this.path + "/deliveryarea/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		CompanyDeliveryAreaDTO companyDeliveryArea = new CompanyDeliveryAreaDTO();
		companyDeliveryArea.setDeliveryAreaZips(new ArrayList<>(Arrays.asList(new String[]{"00000", "00001", "00003"})));
		
		HttpEntity<CompanyDeliveryAreaDTO> entity = new HttpEntity<>(companyDeliveryArea, headers);
		ResponseEntity<CompanyDeliveryAreaDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyDeliveryAreaPath, entity, CompanyDeliveryAreaDTO.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response2.getStatusCode());
	}
	
	@Test
	void putCompanyDeliveryArea() {
		log.info("putCompanyDeliveryArea");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var companyDeliveryAreaPath = this.path + "/deliveryarea/";
		ResponseEntity<CompanyDeliveryAreaDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyDeliveryAreaPath + "{id}", CompanyDeliveryAreaDTO.class, this.companyDeliveryAreaTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		log.info("companyId");
		log.info(dto.getCompanyId().toString());
		var updateEntity = this.companyDeliveryAreaMapper.toEntity(dto);
		updateEntity.setCreateDate(null);
		updateEntity.setDeleted(false);
		updateEntity.setModifyDate(null);
		updateEntity.setCreatedBy(null);
		updateEntity.setLastModifiedBy(null);
		//updateEntity.setCompanyDeliveryArea(this.companyDeliveryAreaMapper.toEntity(dto));
		//updateEntity.setCompany(this.companyDeliveryAreaMapper.toEntity(dto).getCompany());
		//updateEntity.setId(this.companyDeliveryAreaMapper.toEntity(dto).getId());
		updateEntity.setDeliveryAreaZips(new ArrayList<>(Arrays.asList(new String[]{"00000", "00001", "00003"})));
		dto.setDeliveryAreaZips(updateEntity.getDeliveryAreaZips());
		Company comp = new Company();
		comp.setId(dto.getCompanyId());
		
		updateEntity.setId(dto.getId());
		updateEntity.setCompany(comp);
		HttpEntity<CompanyDeliveryAreaDTO> entity = new HttpEntity<>(companyDeliveryAreaMapper.toDto(updateEntity), headers);
		ResponseEntity<CompanyDeliveryAreaDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(companyDeliveryAreaPath + this.companyDeliveryAreaTest.getId(), HttpMethod.PUT, entity, CompanyDeliveryAreaDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.companyDeliveryAreaTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getDeliveryAreaZips(), response2.getBody().getDeliveryAreaZips());
	}
	
	@Test
	void patchCompanyDeliveryArea() {
		log.info("patchCompanyDeliveryAreaCompanyDeliveryArea");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var companyDeliveryAreaPath = this.path + "/deliveryarea/";
		
		
		var request = "{\"deliveryAreaZips\": [\"00000\",\"00003\" ]}";
		log.info(request);
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(companyDeliveryAreaPath + this.companyDeliveryAreaTest.getId(), HttpMethod.PATCH, entity, CompanyDeliveryAreaDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertEquals("[00000, 00003]", response.getBody().getDeliveryAreaZips().toString());
		ResponseEntity<CompanyDeliveryAreaDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/deliveryarea/{id}", CompanyDeliveryAreaDTO.class, this.companyDeliveryAreaTest.getId());
		this.companyDeliveryAreaTest = companyDeliveryAreaMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.companyDeliveryAreaTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.companyDeliveryAreaTest.getDeliveryAreaZips(), response.getBody().getDeliveryAreaZips());
	}
	
	@Test
	void deleteCompanyDeliveryArea() {
		log.info("deleteCompanyDeliveryArea");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/deliveryarea/" + this.companyDeliveryAreaTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CompanyDeliveryAreaDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/deliveryarea/{id}", CompanyDeliveryAreaDTO.class, this.companyDeliveryAreaTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
}

