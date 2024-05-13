package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CompanyCompanyPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyPropertyAssemblerWrapper;
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
@Import(CompanyCompanyPropertyController.class)
public class CompanyCompanyPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CompanyCompanyPropertyController companyPropertyController;
	
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyPropertyMapper companyPropertyMapper;
	@Autowired
	private CompanyCompanyPropertyAssemblerWrapper companyPropertyAssembler;
	private CompanyProperty companyPropertyTest;
	
	private String path;
	
	private UUID packageItemId;
	
	private String pathGlobalProperties;
	
	public CompanyCompanyPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(companyPropertyController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(companyPropertyController);
		
		
		this.companyPropertyTest = setupPackageProperty();
		
		
	}
	
	CompanyProperty setupPackageProperty() throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packageId = super.getDefaultCompany().getId();
		this.packageItemId = packageId;
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1/companies/" + packageId;
		this.pathGlobalProperties = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Webpage");
		compProp1.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.pathGlobalProperties + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		var companyProperty = new CompanyPropertyDTO();
		companyProperty.setKey("Webpage");
		companyProperty.setValue("test-firma.org");
		companyProperty.setType("text");
		
		
		ResponseEntity<CompanyPropertyDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/companyproperties", companyProperty, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, response1.getBody().getId());
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		return companyPropertyMapper.toEntity(response2.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var companyPropertyPath = this.path + "/companyproperties/";
		Assertions.assertNotNull(this.companyPropertyTest);
		Assertions.assertNotNull(this.companyPropertyTest.getId());
		
		Assertions.assertEquals(this.companyPropertyTest, companyPropertyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertyPath + "{id}", CompanyPropertyDTO.class, this.companyPropertyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertyPath + "{id}", String.class, this.companyPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonPackageNotNullValues(response);
		
	}
	
	
	void verifyJsonPackageNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var companyPropertyDTO = companyPropertyAssembler.toModel(this.companyPropertyTest, this.packageItemId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.companyPropertyTest.getId(), this.companyPropertyTest.getCreateDate(), this.companyPropertyTest.getModifyDate(), this.companyPropertyTest.isDeleted());
		
		Assertions.assertEquals(this.companyPropertyTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.companyPropertyTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.companyPropertyTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + companyPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var companyPropertyPath = this.path + "/companyproperties/";
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("testeigenschaft2");
		compProp1.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.pathGlobalProperties + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		var companyProperty = new CompanyPropertyDTO();
		companyProperty.setKey("testeigenschaft2");
		companyProperty.setValue("F");
		companyProperty.setType("string");
		
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/companyproperties", companyProperty, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("companyProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertyPath + "{id}", CompanyPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	public void postEmptyCompanyProperty() {
		log.info("postEmptyCompanyProperty");
		var companyPropertyPath = this.path + "/companyproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyPropertyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postCompanyProperty() {
		log.info("postCompanyPropertyNotFound");
		var companyPropertyPath = this.path + "/companyproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("testeigenschaft3");
		compProp1.setType("boolean");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.pathGlobalProperties + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		CompanyProperty companyProperty = new CompanyProperty();
		companyProperty.setKey("testeigenschaft3");
		companyProperty.setValue("true");
		companyProperty.setType("boolean");
		
		HttpEntity<CompanyPropertyDTO> entity = new HttpEntity<>(companyPropertyMapper.toDto(companyProperty), headers);
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyPropertyPath, entity, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putCompanyProperty() {
		log.info("putCompanyProperty");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var companyPropertyPath = this.path + "/companyproperties/";
		ResponseEntity<CompanyPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPropertyPath + "{id}", CompanyPropertyDTO.class, this.companyPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new CompanyProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<CompanyPropertyDTO> entity = new HttpEntity<>(companyPropertyMapper.toDto(dtoUpdate), headers);
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(companyPropertyPath + this.companyPropertyTest.getId(), HttpMethod.PUT, entity, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.companyPropertyTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	public void patchCompanyProperty() {
		log.info("patchCompanyProperty");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var companyPropertyPath = this.path + "/companyproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(companyPropertyPath + this.companyPropertyTest.getId(), HttpMethod.PATCH, entity, CompanyPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<CompanyPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, this.companyPropertyTest.getId());
		this.companyPropertyTest = companyPropertyMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.companyPropertyTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.companyPropertyTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	public void deleteCompanyProperty() {
		log.info("deleteCompanyProperty");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/companyproperties/" + this.companyPropertyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CompanyPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/companyproperties/{id}", CompanyPropertyDTO.class, this.companyPropertyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

