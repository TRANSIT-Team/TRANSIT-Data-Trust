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
import com.transit.backend.datalayers.controller.CarCarLocationController;
import com.transit.backend.datalayers.controller.CarController;
import com.transit.backend.datalayers.controller.CarPropertyController;
import com.transit.backend.datalayers.controller.assembler.CarAssembler;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.mapper.CarMapper;
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
@Import({CarController.class, CarPropertyController.class})
public class CarControllerTest extends BaseIntegrationTest {
	
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@InjectMocks
	private CarController carController;
	@InjectMocks
	private CarPropertyController carCarPropertyController;
	@InjectMocks
	private CarCarLocationController carLocationController;
	@Autowired
	private CarMapper carMapper;
	@Autowired
	private CarAssembler carAssembler;
	
	
	private Car carTest;
	
	private String path;
	
	private int count;
	
	
	public CarControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	
	@Test
	void contextLoads() {
		Assertions.assertNotNull(carController);
		Assertions.assertNotNull(carCarPropertyController);
	}
	
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(carController);
		Assertions.assertNotNull(carLocationController);
		Assertions.assertNotNull(carCarPropertyController);
		this.count = 0;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.carTest = setupCar(path);
		
		
	}
	
	Car setupCar(String path) {
		
		
		var car = new Car();
		car.setType("VAN");
		car.setCapacity("12341324");
		car.setPlate("4234");
		car.setWeight("32532532");
		
		
		var carProperties1 = new CarPropertyDTO();
		carProperties1.setKey("Glas");
		carProperties1.setValue("Vorsicht zerbrechlich");
		carProperties1.setType("string");
		
		var carProperties2 = new CarPropertyDTO();
		carProperties2.setKey("Gefahrgutklasse");
		carProperties2.setValue("3");
		carProperties2.setType("int");
		
		var carProperties3 = new CarPropertyDTO();
		carProperties3.setKey("Gefahrengrad");
		carProperties3.setValue("F");
		carProperties3.setType("char");
		var geoFactory = new GeometryFactory();
		var carLocation = new LocationDTO();
		carLocation.setLocationPoint(geoFactory.createPoint(new Coordinate(50.32532, 50.12412341)));
		var carLocation2 = new LocationDTO();
		carLocation2.setLocationPoint(geoFactory.createPoint(new Coordinate(50.32532, 50.12412341)));
		
		
		ResponseEntity<CarDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars", car, CarDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		
		
		ResponseEntity<CarProperty> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars/{id}/carproperties", carProperties1, CarProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.count += 1;
		ResponseEntity<CarProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars/{id}/carproperties", carProperties2, CarProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		this.count += 1;
		ResponseEntity<CarProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars/{id}/carproperties", carProperties3, CarProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		this.count += 1;
		
		ResponseEntity<Location> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars/{id}/carlocations", carProperties3, Location.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		
		ResponseEntity<Location> response11 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/cars/{id}/carlocations", carProperties3, Location.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		
		
		ResponseEntity<CarDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/cars/{id}", CarDTO.class, response2.getBody().getId());
		return carMapper.toEntity(response9.getBody());
	}
	
	
	@Test
	void getReturnsCorrectResponseCar() throws Exception {
		
		var carPath = path + "/cars/";
		Assertions.assertNotNull(this.carTest);
		Assertions.assertNotNull(this.carTest.getCapacity());
		
		Assertions.assertEquals(this.carTest, carMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "{id}", CarDTO.class, this.carTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "{id}", String.class, this.carTest.getId());
		ResponseEntity<CarDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "{id}", CarDTO.class, this.carTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonCarNotNullValuesCar(path, response, response2);
		
	}
	
	
	void verifyJsonCarNotNullValuesCar(String path, final ResponseEntity<String> response, ResponseEntity<CarDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var carDTO = carAssembler.toModel(this.carTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.carTest.getId(), this.carTest.getCreateDate(), this.carTest.getModifyDate(), this.carTest.isDeleted());
		
		Assertions.assertEquals(this.carTest.getType(), root.get("type").asText());
		Assertions.assertEquals(this.carTest.getCapacity(), root.get("capacity").asText());
		Assertions.assertEquals(this.carTest.getPlate(), root.get("plate").asText());
		Assertions.assertEquals(this.carTest.getWeight(), root.get("weight").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + carDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
		verifyJsonCarPropertiesNotNullValues(path, root.get("carProperties"), carDTO);
		
	}
	
	
	void verifyJsonCarPropertiesNotNullValues(String path, JsonNode node, CarDTO carDTO) throws Exception {
		
		var carPropertiesIterator = this.carTest.getCarProperties().iterator();
		for (int z = 0; z < this.carTest.getCarProperties().size(); z++) {
			CarProperty carCarProperty = carPropertiesIterator.next();
			CarPropertyDTO carCarPropertyDTO = carDTO.getCarProperties().stream().filter(carPropertiesDTO1 -> carPropertiesDTO1.getId().equals(carCarProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getCarPropertiesById = filter(
					where("id").is(carCarProperty.getId().toString())
			);
			
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getCarPropertiesById);
			JsonNode nodeCarProperties = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodeCarProperties, carCarProperty.getId(), carCarProperty.getCreateDate(), carCarProperty.getModifyDate(), carCarProperty.isDeleted());
			Assertions.assertEquals(carCarProperty.getKey(), nodeCarProperties.get("key").asText());
			Assertions.assertEquals(carCarProperty.getValue(), nodeCarProperties.get("value").asText());
			Assertions.assertEquals(carCarProperty.getType(), nodeCarProperties.get("type").asText());
			JsonNode self = nodeCarProperties.get("_links").get("self");
			Assertions.assertEquals(Link.of(path + carCarPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	@Test
	void getReturnsNotFound() throws Exception {
		setupCar(path);
		var carPath = path + "/cars/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath, String.class, this.carTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("cars").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<CarDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "{id}", CarDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	void postEmptyCar() throws JsonProcessingException {
		log.info("postEmptyCar");
		var carPath = path + "/cars/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		requestJson = "{\"weight\":\"Testauto12\"}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		
	}
	
	@Test
	void postCar() throws JsonProcessingException {
		log.info("postCar");
		var carPath = path + "/cars/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String requestJson = "{\"weight\": \"TestFirma14\",\"carProperties\":[{          " +
				"             \"key\": \"Webseite\"," +
				"            \"value\": \"test.com\"," +
				"            \"type\": \"text\"}]}";
		
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(carPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response2.getStatusCode());
	}
	
	@Test
	void putCarWithCar() throws JsonProcessingException {
		log.info("put Car ");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var carPath = path + "/cars/";
		ResponseEntity<CarDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/cars/{id}", CarDTO.class, this.carTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		dto.setId(null);
		SortedSet<CarPropertyDTO> carProperties = new TreeSet<>();
		var property1 = new CarPropertyDTO();
		property1.setKey("Key");
		property1.setValue("Value");
		property1.setType("Type");
		carProperties.add(property1);
		this.count += 1;
		
		dto.setCarProperties(carProperties);
		dto.setCreateDate(null);
		dto.setModifyDate(null);
		dto.setCreatedBy(null);
		dto.setLastModifiedBy(null);
		dto.setDeleted(false);
		dto.setWeight("43265432643");
		dto.removeLinks();
		
		HttpEntity<CarDTO> entity = new HttpEntity<>(dto, headers);
		
		
		getRestTemplateGenerator().getTestRestTemplate().exchange(carPath + this.carTest.getId(), HttpMethod.PUT, entity, CarDTO.class);
		ResponseEntity<CarDTO> response2 = response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + this.carTest.getId(), CarDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getWeight(), response2.getBody().getWeight());
		Assertions.assertNotNull(response2.getBody().getWeight());
		
		Assertions.assertEquals(this.carTest.getId(), response2.getBody().getId());
		log.info(response2.getBody().getCarProperties().toString());
		Assertions.assertEquals(1, response2.getBody().getCarProperties().size());
		
		Assertions.assertEquals(4, this.count);
		for (var property : response2.getBody().getCarProperties()) {
			if (!property.getKey().equals(property1.getKey())) {
				Assertions.assertTrue(property.isDeleted());
			}
		}
		
	}
	
	@Test
	void patchCar() throws IOException {
		log.info("patchCar");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var carPath = path + "/cars/";
		ResponseEntity<CarDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/cars/{id}", CarDTO.class, this.carTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		HttpEntity<String> entity;
		ResponseEntity<CarDTO> response2;
		
		String request = "{\"weight\":\"" + dto.getWeight() + "\"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, CarDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getWeight(), response2.getBody().getWeight());
		Assertions.assertNotNull(response2.getBody().getWeight());
		Assertions.assertEquals(this.carTest.getId(), response2.getBody().getId());
		
		request = "{\"weight\":\"" + dto.getWeight() + "\"," +
				"        \"carProperties\": [\n" +
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
		
		response2 = template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, CarDTO.class);
		log.info(response2.getBody().getCarProperties().toString());
		request = "{\"weight\":\"" + dto.getWeight() + "\"," +
				"        \"carProperties\": [\n" +
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
		
		response3 = template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, CarDTO.class);
		
		for (var i : response2.getBody().getCarProperties()) {
			for (var j : response3.getBody().getCarProperties()) {
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
		
		
		request = "{\"weight\":\"" + dto.getWeight() + "\"," +
				"        \"carProperties\": [\n" +
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
		response3 = template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, CarDTO.class);
		Assertions.assertEquals(3, response3.getBody().getCarProperties().size());
		request = "{\"weight\":\"" + dto.getWeight() + "\"," +
				"        \"carProperties\": [\n" +
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
		
		response3 = template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, CarDTO.class);
		Assertions.assertEquals(2, response3.getBody().getCarProperties().size());
		
		template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "?filter=carProperties.deleted==all", String.class);
		var root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("cars");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<CarDTO>>() {
		});
		List<CarDTO> carDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		var iii = 0;
		for (var j : carDTOS) {
			if (j.getId().equals(this.carTest.getId())) {
				
				for (var i : j.getCarProperties()) {
					iii = j.getCarProperties().size();
					
					
					if (i.isDeleted()) {
						log.info(i.getKey());
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
		//log.info(node.toPrettyString());
		log.info(String.valueOf(carDTOS.size()));
		log.info(String.valueOf(iii));
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(2, notdel);
		
		template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "?filter=carProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("cars");
		reader = objectMapper.readerFor(new TypeReference<List<CarDTO>>() {
		});
		carDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : carDTOS) {
			if (j.getId().equals(this.carTest.getId())) {
				for (var i : j.getCarProperties()) {
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
		
		template.exchange(carPath + this.carTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(carPath + "?filter=carProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("cars");
		reader = objectMapper.readerFor(new TypeReference<List<CarDTO>>() {
		});
		carDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		
		for (var j : carDTOS) {
			if (j.getId().equals(this.carTest.getId())) {
				for (var i : j.getCarProperties()) {
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
	void deleteCar() {
		log.info("deleteCar");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/cars/" + this.carTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<CarDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/cars/{id}", CarDTO.class, this.carTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getCarProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
}

