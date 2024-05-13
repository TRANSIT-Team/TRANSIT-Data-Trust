package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyPropertyController;
import com.transit.backend.datalayers.controller.assembler.CompanyAssembler;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.mapper.CompanyMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

@Slf4j
@Import({CompanyController.class, CompanyPropertyController.class})
public class CompanyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	CompanyController companyController;
	
	
	@InjectMocks
	CompanyPropertyController companyCompanyPropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private CompanyAssembler companyAssembler;
	
	
	private Company companyTest;
	
	private String path;
	
	private int count;
	
	public CompanyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(companyController);
		Assertions.assertNotNull(companyCompanyPropertyController);
	}
	
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(companyController);
		Assertions.assertNotNull(companyCompanyPropertyController);
		this.count = 3;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.companyTest = setupCompany(path);
		
		
	}
	
	Company setupCompany(String path) {
		
		
		return super.getDefaultCompany();
		
	}
	
	
	@Test
	public void getReturnsCorrectResponseCompany() throws Exception {
		
		var companyPath = path + "/companies/";
		Assertions.assertNotNull(this.companyTest);
		
		Assertions.assertEquals(this.companyTest, companyMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "{id}", CompanyDTO.class, this.companyTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "{id}", String.class, this.companyTest.getId());
		ResponseEntity<CompanyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "{id}", CompanyDTO.class, this.companyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonCompanyNotNullValuesCompany(path, response, response2);
		
	}
	
	
	void verifyJsonCompanyNotNullValuesCompany(String path, final ResponseEntity<String> response, ResponseEntity<CompanyDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var companyDTO = companyAssembler.toModel(this.companyTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.companyTest.getId(), this.companyTest.getCreateDate(), this.companyTest.getModifyDate(), this.companyTest.isDeleted());
		
		Assertions.assertEquals(this.companyTest.getName(), root.get("name").asText());
		
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + companyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
		verifyJsonCompanyPropertiesNotNullValues(path, root.get("companyProperties"), companyDTO);
		
	}
	
	
	void verifyJsonCompanyPropertiesNotNullValues(String path, JsonNode node, CompanyDTO companyDTO) throws Exception {
		
		var companyPropetyIterator = this.companyTest.getCompanyProperties().iterator();
		for (int z = 0; z < this.companyTest.getCompanyProperties().size(); z++) {
			CompanyProperty packageCompanyProperty = companyPropetyIterator.next();
			CompanyPropertyDTO packageCompanyPropertyDTO = companyDTO.getCompanyProperties().stream().filter(companyPropetyDTO1 -> companyPropetyDTO1.getId().equals(packageCompanyProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getCompanypropertyById = filter(
					where("id").is(packageCompanyProperty.getId().toString())
			);
			
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getCompanypropertyById);
			JsonNode nodeCompanyproperty = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodeCompanyproperty, packageCompanyProperty.getId(), packageCompanyProperty.getCreateDate(), packageCompanyProperty.getModifyDate(), packageCompanyProperty.isDeleted());
			Assertions.assertEquals(packageCompanyProperty.getKey(), nodeCompanyproperty.get("key").asText());
			Assertions.assertEquals(packageCompanyProperty.getValue(), nodeCompanyproperty.get("value").asText());
			Assertions.assertEquals(packageCompanyProperty.getType(), nodeCompanyproperty.get("type").asText());
			JsonNode self = nodeCompanyproperty.get("_links").get("self");
			Assertions.assertEquals(Link.of(path + packageCompanyPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	@Test
	public void getReturnsNotFound() throws Exception {
		setupCompany(path);
		var companyPath = path + "/companies/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath, String.class, this.companyTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("companies").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CompanyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "{id}", CompanyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	public void postEmptyCompany() throws JsonProcessingException {
		log.info("postEmptyCompany");
		var companyPath = path + "/companies/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		requestJson = "{\"name\":\"Testfirma12\"}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		
	}
	
	@Test
	public void postCompany() throws JsonProcessingException {
		log.info("postCompany");
		var companyPath = path + "/companies/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String requestJson = "{\"name\": \"TestFirma14\",\"companyProperties\":[{          " +
				"             \"key\": \"Webseite\"," +
				"            \"value\": \"test.com\"," +
				"            \"type\": \"text\"}]}";
		
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(companyPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());
	}
	
	@Test
	public void putCompanyWithCompany() throws JsonProcessingException {
		log.info("put Company ");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Key");
		compProp1.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		var companyPath = path + "/companies/";
		ResponseEntity<CompanyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/companies/{id}", CompanyDTO.class, this.companyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		dto.setId(null);
		SortedSet<CompanyPropertyDTO> companyProperties = new TreeSet<>();
		var property1 = new CompanyPropertyDTO();
		property1.setKey("Key");
		property1.setValue("Value");
		property1.setType("Type");
		companyProperties.add(property1);
		this.count += 1;
		
		dto.setCompanyProperties(companyProperties);
		dto.setCreateDate(null);
		dto.setModifyDate(null);
		dto.setCreatedBy(null);
		dto.setLastModifiedBy(null);
		dto.setDeleted(false);
		dto.setName("testtestname");
		dto.removeLinks();
		
		HttpEntity<CompanyDTO> entity = new HttpEntity<>(dto, headers);
		
		
		getRestTemplateGenerator().getTestRestTemplate().exchange(companyPath + this.companyTest.getId(), HttpMethod.PUT, entity, CompanyDTO.class);
		ResponseEntity<CompanyDTO> response2 = response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + this.companyTest.getId(), CompanyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getName(), response2.getBody().getName());
		Assertions.assertNotNull(response2.getBody().getName());
		
		Assertions.assertEquals(this.companyTest.getId(), response2.getBody().getId());
		log.info(response2.getBody().getCompanyProperties().toString());
		Assertions.assertEquals(1, response2.getBody().getCompanyProperties().size());
		
		Assertions.assertEquals(4, this.count);
		for (var property : response2.getBody().getCompanyProperties()) {
			if (!property.getKey().equals(property1.getKey())) {
				Assertions.assertTrue(property.isDeleted());
			}
		}
		
	}
	
	@Test
	public void patchCompany() throws IOException {
		log.info("patchCompany");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Anzahl Mitarbeiter");
		compProp1.setType("boolean");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Anzahl Führungsetage");
		compProp1.setType("boolean");
		
		response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Anzahl Teilfirmen");
		compProp1.setType("boolean");
		
		response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		var companyPath = path + "/companies/";
		ResponseEntity<CompanyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/companies/{id}", CompanyDTO.class, this.companyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		HttpEntity<String> entity;
		ResponseEntity<CompanyDTO> response2;
		
		String request = "{\"name\":\"" + dto.getName() + "\"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, CompanyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getName(), response2.getBody().getName());
		Assertions.assertNotNull(response2.getBody().getName());
		Assertions.assertEquals(this.companyTest.getId(), response2.getBody().getId());
		
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"companyProperties\": [\n" +
				"            {\"key\":\"Anzahl Mitarbeiter\",\n" +
				"            \"value\":\"5344\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"Anzahl Führungsetage\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, CompanyDTO.class);
		log.info(response2.getBody().getCompanyProperties().toString());
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"companyProperties\": [\n" +
				"            {\"key\":\"Anzahl Mitarbeiter\",\n" +
				"            \"value\":\"53\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"Anzahl Führungsetage\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, CompanyDTO.class);
		
		for (var i : response2.getBody().getCompanyProperties()) {
			for (var j : response3.getBody().getCompanyProperties()) {
				if (i.getKey() == j.getKey()) {
					Assertions.assertEquals(i.getId(), j.getId());
					Assertions.assertEquals(i.getType(), j.getType());
					Assertions.assertEquals(i.isDeleted(), j.isDeleted());
					Assertions.assertEquals(i.getCreateDate(), j.getCreateDate());
					Assertions.assertNotEquals(i.getValue(), j.getValue());
					Assertions.assertNotEquals(i.getModifyDate(), j.getModifyDate());
				}
			}
		}
		
		
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"companyProperties\": [\n" +
				"            {\"key\":\"Anzahl Mitarbeiter\",\n" +
				"            \"value\":\"53\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"Anzahl Führungsetage\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n," +
				"            {\"key\":\"Anzahl Teilfirmen\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		
		entity = new HttpEntity<>(request, headers);
		response3 = template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, CompanyDTO.class);
		Assertions.assertEquals(3, response3.getBody().getCompanyProperties().size());
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"companyProperties\": [\n" +
				"            {\"key\":\"Anzahl Mitarbeiter\",\n" +
				"            \"value\":\"53\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"Anzahl Führungsetage\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, CompanyDTO.class);
		Assertions.assertEquals(2, response3.getBody().getCompanyProperties().size());
		
		template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "?filter=companyProperties.deleted==all", String.class);
		var root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("companies");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<CompanyDTO>>() {
		});
		List<CompanyDTO> companyDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		
		for (var j : companyDTOS) {
			if (j.getId().equals(this.companyTest.getId())) {
				for (var i : j.getCompanyProperties()) {
					log.info(i.getKey());
					if (i.isDeleted()) {
						if (i.getKey().equals("Webseite") || i.getKey().equals("Email") || i.getKey().equals("Beschreibung")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("Anzahl Teilfirmen", i.getKey());
						}
					} else {
						notdel += 1;
					}
					
				}
			}
		}
		log.info(node.toPrettyString());
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(2, notdel);
		
		template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "?filter=companyProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("companies");
		reader = objectMapper.readerFor(new TypeReference<List<CompanyDTO>>() {
		});
		companyDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : companyDTOS) {
			if (j.getId().equals(this.companyTest.getId())) {
				for (var i : j.getCompanyProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Webseite") || i.getKey().equals("Email") || i.getKey().equals("Beschreibung")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("Anzahl Teilfirmen", i.getKey());
						}
					} else {
						notdel += 1;
					}
				}
				
			}
		}
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(0, notdel);
		
		template.exchange(companyPath + this.companyTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(companyPath + "?filter=companyProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("companies");
		reader = objectMapper.readerFor(new TypeReference<List<CompanyDTO>>() {
		});
		companyDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		
		for (var j : companyDTOS) {
			if (j.getId().equals(this.companyTest.getId())) {
				for (var i : j.getCompanyProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Webseite") || i.getKey().equals("Email") || i.getKey().equals("Beschreibung")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("Anzahl Teilfirmen", i.getKey());
						}
					} else {
						notdel += 1;
					}
					
				}
			}
		}
		Assertions.assertEquals(0, del);
		Assertions.assertEquals(2, notdel);
	}
	
	@Test
	public void deleteCompany() {
		log.info("deleteCompany");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/companies/" + this.companyTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CompanyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/companies/{id}", CompanyDTO.class, this.companyTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getCompanyProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
}
