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
import com.transit.backend.datalayers.controller.WarehouseController;
import com.transit.backend.datalayers.controller.WarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.WarehouseAssembler;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.mapper.WarehouseMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
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

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

@Slf4j
@Import({WarehouseController.class, WarehousePropertyController.class})
public class WarehouseControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	WarehouseController warehouseController;
	
	
	@InjectMocks
	WarehousePropertyController warehouseWarehousePropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WarehouseMapper warehouseMapper;
	@Autowired
	private WarehouseAssembler warehouseAssembler;
	
	
	private Warehouse warehouseTest;
	
	private String path;
	
	private int count;
	
	public WarehouseControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(warehouseController);
		Assertions.assertNotNull(warehouseWarehousePropertyController);
	}
	
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(warehouseController);
		Assertions.assertNotNull(warehouseWarehousePropertyController);
		this.count = 0;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.warehouseTest = setupWarehouse(path);
		
		
	}
	
	Warehouse setupWarehouse(String path) {
		
		var warehouse = new Warehouse();
		warehouse.setName("testWarehouse");
		warehouse.setCapacity(12341324l);
		
		
		var warehouseProperties1 = new WarehousePropertyDTO();
		warehouseProperties1.setKey("Glas");
		warehouseProperties1.setValue("Vorsicht zerbrechlich");
		warehouseProperties1.setType("string");
		
		var warehouseProperties2 = new WarehousePropertyDTO();
		warehouseProperties2.setKey("Gefahrgutklasse");
		warehouseProperties2.setValue("3");
		warehouseProperties2.setType("int");
		
		var warehouseProperties3 = new WarehousePropertyDTO();
		warehouseProperties3.setKey("Gefahrengrad");
		warehouseProperties3.setValue("F");
		warehouseProperties3.setType("char");
		var geoFactory = new GeometryFactory();
		var warehouseaddress = new AddressDTO();
		warehouseaddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50.32532, 50.12412341)));
		warehouseaddress.setCity("TestStadt");
		warehouseaddress.setState("TestState");
		warehouseaddress.setCountry("TestLand");
		warehouseaddress.setStreet("Teststrasse");
		warehouseaddress.setIsoCode("DE");
		warehouseaddress.setZip("11111");
		warehouseaddress.setAddressExtra("50. Etage");
		
		
		ResponseEntity<WarehouseDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses", warehouse, WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		
		
		ResponseEntity<WarehouseProperty> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses/{id}/warehouseproperties", warehouseProperties1, WarehouseProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.count += 1;
		ResponseEntity<WarehouseProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses/{id}/warehouseproperties", warehouseProperties2, WarehouseProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		this.count += 1;
		ResponseEntity<WarehouseProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses/{id}/warehouseproperties", warehouseProperties3, WarehouseProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		this.count += 1;
		
		ResponseEntity<AddressDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/warehouses/{id}/warehouseaddresses", warehouseaddress, AddressDTO.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		
		ResponseEntity<WarehouseDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/warehouses/{id}", WarehouseDTO.class, response2.getBody().getId());
		return warehouseMapper.toEntity(response9.getBody());
		
	}
	
	
	@Test
	void getReturnsCorrectResponseWarehouse() throws Exception {
		
		var warehousePath = path + "/warehouses/";
		Assertions.assertNotNull(this.warehouseTest);
		
		Assertions.assertEquals(this.warehouseTest, warehouseMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "{id}", WarehouseDTO.class, this.warehouseTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "{id}", String.class, this.warehouseTest.getId());
		ResponseEntity<WarehouseDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "{id}", WarehouseDTO.class, this.warehouseTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonWarehouseNotNullValuesWarehouse(path, response, response2);
		
	}
	
	
	void verifyJsonWarehouseNotNullValuesWarehouse(String path, final ResponseEntity<String> response, ResponseEntity<WarehouseDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var warehouseDTO = warehouseAssembler.toModel(this.warehouseTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.warehouseTest.getId(), this.warehouseTest.getCreateDate(), this.warehouseTest.getModifyDate(), this.warehouseTest.isDeleted());
		
		Assertions.assertEquals(this.warehouseTest.getName(), root.get("name").asText());
		Assertions.assertEquals(this.warehouseTest.getCapacity(), Long.valueOf(root.get("capacity").asText()));
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + warehouseDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
		verifyJsonWarehousePropertiesNotNullValues(path, root.get("warehouseProperties"), warehouseDTO);
		
	}
	
	
	void verifyJsonWarehousePropertiesNotNullValues(String path, JsonNode node, WarehouseDTO warehouseDTO) throws Exception {
		
		var warehousePropetyIterator = this.warehouseTest.getWarehouseProperties().iterator();
		for (int z = 0; z < this.warehouseTest.getWarehouseProperties().size(); z++) {
			WarehouseProperty packageWarehouseProperty = warehousePropetyIterator.next();
			WarehousePropertyDTO packageWarehousePropertyDTO = warehouseDTO.getWarehouseProperties().stream().filter(warehousePropetyDTO1 -> warehousePropetyDTO1.getId().equals(packageWarehouseProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getWarehousepropertyById = filter(
					where("id").is(packageWarehouseProperty.getId().toString())
			);
			
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getWarehousepropertyById);
			JsonNode nodeWarehouseproperty = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodeWarehouseproperty, packageWarehouseProperty.getId(), packageWarehouseProperty.getCreateDate(), packageWarehouseProperty.getModifyDate(), packageWarehouseProperty.isDeleted());
			Assertions.assertEquals(packageWarehouseProperty.getKey(), nodeWarehouseproperty.get("key").asText());
			Assertions.assertEquals(packageWarehouseProperty.getValue(), nodeWarehouseproperty.get("value").asText());
			Assertions.assertEquals(packageWarehouseProperty.getType(), nodeWarehouseproperty.get("type").asText());
			JsonNode self = nodeWarehouseproperty.get("_links").get("self");
			Assertions.assertEquals(Link.of(path + packageWarehousePropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	@Test
	void getReturnsNotFound() throws Exception {
		setupWarehouse(path);
		var warehousePath = path + "/warehouses/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath, String.class, this.warehouseTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("warehouses").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<WarehouseDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "{id}", WarehouseDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	void postEmptyWarehouse() throws JsonProcessingException {
		log.info("postEmptyWarehouse");
		var warehousePath = path + "/warehouses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehousePath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		requestJson = "{\"name\":\"Testfirma12\"}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehousePath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
	}
	
	@Test
	void postWarehouse() throws JsonProcessingException {
		log.info("postWarehouse");
		var warehousePath = path + "/warehouses/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String requestJson = "{\"name\": \"TestFirma14\",\"warehouseProperties\":[{          " +
				"             \"key\": \"Webseite\"," +
				"            \"value\": \"test.com\"," +
				"            \"type\": \"text\"}]}";
		
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(warehousePath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	void putWarehouseWithWarehouse() throws JsonProcessingException {
		log.info("put Warehouse ");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var warehousePath = path + "/warehouses/";
		ResponseEntity<WarehouseDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/warehouses/{id}", WarehouseDTO.class, this.warehouseTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		dto.setId(null);
		SortedSet<WarehousePropertyDTO> warehouseProperties = new TreeSet<>();
		var property1 = new WarehousePropertyDTO();
		property1.setKey("Key");
		property1.setValue("Value");
		property1.setType("Type");
		warehouseProperties.add(property1);
		this.count += 1;
		
		dto.setWarehouseProperties(warehouseProperties);
		dto.setCreateDate(null);
		dto.setModifyDate(null);
		dto.setCreatedBy(null);
		dto.setLastModifiedBy(null);
		dto.setDeleted(false);
		dto.setName("testtestname");
		dto.removeLinks();
		
		HttpEntity<WarehouseDTO> entity = new HttpEntity<>(dto, headers);
		
		
		getRestTemplateGenerator().getTestRestTemplate().exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PUT, entity, WarehouseDTO.class);
		ResponseEntity<WarehouseDTO> response2 = response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + this.warehouseTest.getId(), WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getName(), response2.getBody().getName());
		Assertions.assertNotNull(response2.getBody().getName());
		
		Assertions.assertEquals(this.warehouseTest.getId(), response2.getBody().getId());
		log.info(response2.getBody().getWarehouseProperties().toString());
		Assertions.assertEquals(1, response2.getBody().getWarehouseProperties().size());
		
		Assertions.assertEquals(4, this.count);
		for (var property : response2.getBody().getWarehouseProperties()) {
			if (!property.getKey().equals(property1.getKey())) {
				Assertions.assertTrue(property.isDeleted());
			}
		}
		
	}
	
	@Test
	void patchWarehouse() throws IOException {
		log.info("patchWarehouse");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var warehousePath = path + "/warehouses/";
		ResponseEntity<WarehouseDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/warehouses/{id}", WarehouseDTO.class, this.warehouseTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		HttpEntity<String> entity;
		ResponseEntity<WarehouseDTO> response2;
		
		String request = "{\"name\":\"" + dto.getName() + "\"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, WarehouseDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getName(), response2.getBody().getName());
		Assertions.assertNotNull(response2.getBody().getName());
		Assertions.assertEquals(this.warehouseTest.getId(), response2.getBody().getId());
		
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"warehouseProperties\": [\n" +
				"            {\"key\":\" Anzahl Mitarbeiter\",\n" +
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
		
		response2 = template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, WarehouseDTO.class);
		log.info(response2.getBody().getWarehouseProperties().toString());
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"warehouseProperties\": [\n" +
				"            {\"key\":\" Anzahl Mitarbeiter\",\n" +
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
		
		response3 = template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, WarehouseDTO.class);
		
		for (var i : response2.getBody().getWarehouseProperties()) {
			for (var j : response3.getBody().getWarehouseProperties()) {
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
				"        \"warehouseProperties\": [\n" +
				"            {\"key\":\" Anzahl Mitarbeiter\",\n" +
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
		response3 = template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, WarehouseDTO.class);
		Assertions.assertEquals(3, response3.getBody().getWarehouseProperties().size());
		request = "{\"name\":\"" + dto.getName() + "\"," +
				"        \"warehouseProperties\": [\n" +
				"            {\"key\":\" Anzahl Mitarbeiter\",\n" +
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
		
		response3 = template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, WarehouseDTO.class);
		Assertions.assertEquals(2, response3.getBody().getWarehouseProperties().size());
		
		template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "?filter=warehouseProperties.deleted==all", String.class);
		var root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("warehouses");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<WarehouseDTO>>() {
		});
		List<WarehouseDTO> warehouseDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		
		for (var j : warehouseDTOS) {
			if (j.getId().equals(this.warehouseTest.getId())) {
				for (var i : j.getWarehouseProperties()) {
					log.info(i.getKey());
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
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
		
		template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "?filter=warehouseProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("warehouses");
		reader = objectMapper.readerFor(new TypeReference<List<WarehouseDTO>>() {
		});
		warehouseDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : warehouseDTOS) {
			if (j.getId().equals(this.warehouseTest.getId())) {
				for (var i : j.getWarehouseProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
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
		
		template.exchange(warehousePath + this.warehouseTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(warehousePath + "?filter=warehouseProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("warehouses");
		reader = objectMapper.readerFor(new TypeReference<List<WarehouseDTO>>() {
		});
		warehouseDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		
		for (var j : warehouseDTOS) {
			if (j.getId().equals(this.warehouseTest.getId())) {
				for (var i : j.getWarehouseProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
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
	void deleteWarehouse() {
		log.info("deleteWarehouse");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/warehouses/" + this.warehouseTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<WarehouseDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/warehouses/{id}", WarehouseDTO.class, this.warehouseTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getWarehouseProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
}
