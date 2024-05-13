package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CompanyPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
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
@Import(CompanyPropertyController.class)
public class CompanyPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CompanyPropertyController companyPropertiesController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyPropertyMapper companyPropertiesMapper;
	@Autowired
	private CompanyPropertyAssemblerWrapper companyPropertiesAssembler;
	private CompanyProperty companyPropertiesTest;
	
	private String path;
	
	private UUID companyItemIdrel;
	
	private String pathGlobalProperties;
	
	public CompanyPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(companyPropertiesController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(companyPropertiesController);
		
		
		this.companyPropertiesTest = setupCompanyProperty();
		
		
	}
	
	CompanyProperty setupCompanyProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		
		var companyId = super.getDefaultCompany().getId();
		this.companyItemIdrel = companyId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + companyId;
		this.pathGlobalProperties = getBASE_PATH() + getRandomServerPort() + "/api/v1/";
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Websssssssssseite");
		compProp1.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.pathGlobalProperties + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Web");
		compProp1.setType("text");
		
		response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.pathGlobalProperties + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		var companyProperties = new CompanyPropertyDTO();
		companyProperties.setKey("Web");
		companyProperties.setValue("test-project.com");
		companyProperties.setType("text");
		
		
		ResponseEntity<CompanyPropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/companyproperties", companyProperties, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		return companyPropertiesMapper.toEntity(response2.getBody());
		
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var companyPropertiesPath = this.path + "/companyproperties/";
		Assertions.assertNotNull(this.companyPropertiesTest);
		Assertions.assertNotNull(this.companyPropertiesTest.getId());
		
		Assertions.assertEquals(this.companyPropertiesTest, companyPropertiesMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertiesPath + "{id}", CompanyPropertyDTO.class, this.companyPropertiesTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertiesPath + "{id}", String.class, this.companyPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonCompanyNotNullValues(response);
		
	}
	
	
	void verifyJsonCompanyNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var companyPropertiesDTO = companyPropertiesAssembler.toModel(this.companyPropertiesTest, this.companyItemIdrel, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.companyPropertiesTest.getId(), this.companyPropertiesTest.getCreateDate(), this.companyPropertiesTest.getModifyDate(), this.companyPropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.companyPropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.companyPropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.companyPropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + companyPropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var companyPropertiesPath = this.path + "/companyproperties/";
		var companyProperties = new CompanyPropertyDTO();
		companyProperties.setKey("Web");
		companyProperties.setValue("test.com");
		companyProperties.setType("text");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + this.companyItemIdrel + "/companyproperties", companyProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + this.companyItemIdrel + "/companyproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("companyProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertiesPath + "{id}", CompanyPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	
	@Test
	public void putCompanyProperties() {
		log.info("putCompanyProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var companyPropertiesPath = this.path + "/companyproperties/";
		ResponseEntity<CompanyPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertiesPath + "{id}", CompanyPropertyDTO.class, this.companyPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new CompanyProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getType());
		
		HttpEntity<CompanyPropertyDTO> entity = new HttpEntity<>(companyPropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(companyPropertiesPath + this.companyPropertiesTest.getId(), HttpMethod.PUT, entity, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.companyPropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	public void patchCompanyProperties() {
		log.info("patchCompanyProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var companyPropertiesPath = this.path + "/companyproperties/";
		
		
		String request = "{\"value\":\"test-test.com\"}";
		var entity = new HttpEntity<>(request, headers);
		//patch
		
		var response = template.exchange(companyPropertiesPath + this.companyPropertiesTest.getId(), HttpMethod.PATCH, entity, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("test-test.com", response.getBody().getValue());
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, this.companyPropertiesTest.getId());
		this.companyPropertiesTest = companyPropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.companyPropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.companyPropertiesTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	public void deleteCompanyProperties() {
		log.info("deleteCompanyItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/companyproperties/" + this.companyPropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CompanyPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, this.companyPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

