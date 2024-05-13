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
import com.transit.backend.datalayers.controller.PackageItemController;
import com.transit.backend.datalayers.controller.PackageItemPackageClassController;
import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.PackageItemAssembler;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.service.mapper.PackageItemMapper;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

//PackagePropertiers+ sublinks
@Slf4j
@Import({PackageItemController.class, PackageItemPackageClassController.class})
public class PackageItemControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	PackageItemController packageItemController;
	
	@InjectMocks
	PackageItemPackageClassController packageClassController;
	
	@InjectMocks
	PackageItemPackagePackagePropertyController packageItemPackagePackagePropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackageItemMapper packageItemMapper;
	@Autowired
	private PackageItemAssembler packageItemAssembler;
	
	
	private PackageItem packageItemTest;
	
	private String path;
	
	private int count;
	
	public PackageItemControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(packageItemController);
		Assertions.assertNotNull(packageClassController);
	}
	
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(packageItemController);
		Assertions.assertNotNull(packageClassController);
		Assertions.assertNotNull(packageItemPackagePackagePropertyController);
		this.count = 0;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.packageItemTest = setupPackage(path);
		
		
	}
	
	PackageItem setupPackage(String path) {
		return setupPackage(path, null);
	}
	
	PackageItem setupPackage(String path, String packageClassName) {
		var packageItem = new PackageItem();
		packageItem.setHeightCm(12);
		packageItem.setWeightKg(12341324);
		packageItem.setDeepCm(4234);
		packageItem.setWidthCm(32532532);
		
		
		var packageProperties1 = new PackagePropertyDTO();
		packageProperties1.setKey("Glas");
		packageProperties1.setDefaultValue("Vorsicht zerbrechlich");
		packageProperties1.setType("string");
		
		var packageProperties2 = new PackagePropertyDTO();
		packageProperties2.setKey("Gefahrgutklasse");
		packageProperties2.setDefaultValue("3");
		packageProperties2.setType("int");
		
		var packageProperties3 = new PackagePropertyDTO();
		packageProperties3.setKey("Gefahrengrad");
		packageProperties3.setDefaultValue("F");
		packageProperties3.setType("char");
		
		var packageItemPackageProperties1 = new PackagePackagePropertyDTO();
		packageItemPackageProperties1.setKey(packageProperties1.getKey());
		packageItemPackageProperties1.setValue(packageProperties1.getDefaultValue());
		packageItemPackageProperties1.setType(packageProperties1.getType());
		
		
		var packageItemPackageProperties2 = new PackagePackagePropertyDTO();
		packageItemPackageProperties2.setKey(packageProperties2.getKey());
		packageItemPackageProperties2.setValue(packageProperties2.getDefaultValue());
		packageItemPackageProperties2.setType(packageProperties2.getType());
		
		var packageItemPackageProperties3 = new PackagePackagePropertyDTO();
		packageItemPackageProperties3.setKey(packageProperties3.getKey());
		packageItemPackageProperties3.setValue(packageProperties3.getDefaultValue());
		packageItemPackageProperties3.setType(packageProperties3.getType());
		var packageClass = new PackageClassDTO();
		if (packageClassName == null) {
			packageClass.setName("Pappkarton_gepolstert");
		} else {
			packageClass.setName(packageClassName);
		}
		ResponseEntity<PackageClassDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageclasses", packageClass, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		var packageClassAdd = new PackageClass();
		packageClassAdd.setId(response1.getBody().getId());
		packageItem.setPackageClass(packageClassAdd);
		
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems", packageItem, PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		ResponseEntity<PackageProperty> response3 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageproperties", packageProperties1, PackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response3.getStatusCode());
		ResponseEntity<PackageProperty> response4 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageproperties", packageProperties2, PackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response4.getStatusCode());
		ResponseEntity<PackageProperty> response5 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageproperties", packageProperties3, PackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response5.getStatusCode());
		
		ResponseEntity<PackagePackageProperty> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties1, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.count += 1;
		ResponseEntity<PackagePackageProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties2, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		this.count += 1;
		ResponseEntity<PackagePackageProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties3, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		this.count += 1;
		
		ResponseEntity<PackageItemDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, response2.getBody().getId());
		return packageItemMapper.toEntity(response9.getBody());
	}
	
	@Test
	public void getReturnsCorrectResponsePackageItem() throws Exception {
		
		var packageItemPath = path + "/packageitems/";
		Assertions.assertNotNull(this.packageItemTest);
		Assertions.assertNotNull(this.packageItemTest.getPackageClass());
		
		Assertions.assertEquals(this.packageItemTest, packageItemMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "{id}", PackageItemDTO.class, this.packageItemTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "{id}", String.class, this.packageItemTest.getId());
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "{id}", PackageItemDTO.class, this.packageItemTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonPackageNotNullValuesPackageItem(path, response, response2);
		
	}
	
	
	void verifyJsonPackageNotNullValuesPackageItem(String path, final ResponseEntity<String> response, ResponseEntity<PackageItemDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packageItemDTO = packageItemAssembler.toModel(this.packageItemTest);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.packageItemTest.getId(), this.packageItemTest.getCreateDate(), this.packageItemTest.getModifyDate(), this.packageItemTest.isDeleted());
		
		Assertions.assertEquals(this.packageItemTest.getWeightKg(), Double.parseDouble(root.get("weightKg").asText()));
		Assertions.assertEquals(this.packageItemTest.getWidthCm(), Double.parseDouble(root.get("widthCm").asText()));
		Assertions.assertEquals(this.packageItemTest.getHeightCm(), Double.parseDouble(root.get("heightCm").asText()));
		Assertions.assertEquals(this.packageItemTest.getDeepCm(), Double.parseDouble(root.get("deepCm").asText()));
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + packageItemDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		verifyJsonPackagePackageClassNotNullValues(path, root.get("packageClass"), packageItemDTO);
		verifyJsonPackagePackagePropertiesNotNullValues(path, root.get("packagePackageProperties"), packageItemDTO);
		
	}
	
	
	void verifyJsonPackagePackageClassNotNullValues(String path, JsonNode node, PackageItemDTO packageItemDTO) {
		verifyJsonAbstractParameter(node, this.packageItemTest.getPackageClass().getId(), this.packageItemTest.getPackageClass().getCreateDate(), this.packageItemTest.getPackageClass().getModifyDate(), this.packageItemTest.getPackageClass().isDeleted());
		Assertions.assertEquals(this.packageItemTest.getPackageClass().getName(), node.get("name").asText());
		JsonNode self = node.get("_links").get("self");
		Assertions.assertEquals(Link.of(path + packageItemDTO.getPackageClass().getLink("self").get().getHref()), Link.of(self.get("href").asText()));
	}
	
	void verifyJsonPackagePackagePropertiesNotNullValues(String path, JsonNode node, PackageItemDTO packageItemDTO) throws Exception {
		
		var packagePropertiesIterator = this.packageItemTest.getPackagePackageProperties().iterator();
		for (int z = 0; z < this.packageItemTest.getPackagePackageProperties().size(); z++) {
			PackagePackageProperty packagePackageProperty = packagePropertiesIterator.next();
			PackagePackagePropertyDTO packagePackagePropertyDTO = packageItemDTO.getPackagePackageProperties().stream().filter(packagePropertiesDTO1 -> packagePropertiesDTO1.getId().equals(packagePackageProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getPackagePropertiesById = filter(
					where("id").is(packagePackageProperty.getId().toString())
			);
			
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getPackagePropertiesById);
			JsonNode nodePackageProperties = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodePackageProperties, packagePackageProperty.getId(), packagePackageProperty.getCreateDate(), packagePackageProperty.getModifyDate(), packagePackageProperty.isDeleted());
			Assertions.assertEquals(packagePackageProperty.getKey(), nodePackageProperties.get("key").asText());
			Assertions.assertEquals(packagePackageProperty.getValue(), nodePackageProperties.get("value").asText());
			Assertions.assertEquals(packagePackageProperty.getType(), nodePackageProperties.get("type").asText());
			JsonNode self = nodePackageProperties.get("_links").get("self");
			Assertions.assertEquals(Link.of(path + packagePackagePropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	@Test
	public void getReturnsNotFound() throws Exception {
		setupPackage(path);
		var packageItemPath = path + "/packageitems/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath, String.class, this.packageItemTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageItems").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		Assertions.assertNotNull(root.get("page"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "{id}", PackageItemDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	public void postEmptyPackage() throws JsonProcessingException {
		log.info("postEmptyPackage");
		var packageItemPath = path + "/packageitems/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		var packageClass = new PackageClass();
		packageClass.setName("Pappkarton_gepolstert");
		
		
		ResponseEntity<PackageClassDTO> responseC = getRestTemplateGenerator().getTestRestTemplate().postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1/packageclasses", packageClass, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, responseC.getStatusCode());
		requestJson = "{\"packageClass\":{\"id\":\"" + responseC.getBody().getId() + "\"}}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		//Test filter by empty PackageProperties
		JsonNode root = objectMapper.readTree(response.getBody());
		JsonNode self = root.get("_links").get("self");
		
		ResponseEntity<PackageItemDTO> responseD = getRestTemplateGenerator().getTestRestTemplate().getForEntity(self.get("href").asText() + "?filter=packagePackageProperties.deleted==false", PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.OK, responseD.getStatusCode());
		ResponseEntity<String> responseE = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "?filter= packagePackageProperties.deleted==false;deleted==false", String.class);
		Assertions.assertEquals(HttpStatus.OK, responseE.getStatusCode());
	}
	
	@Test
	public void postPackage() throws JsonProcessingException {
		log.info("postPackage");
		var packageItemPath = path + "/packageitems/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageClasses").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		String requestJson = "{\"packageClass\":{\"id\":\"" + testUUID + "\"}}";
		
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		requestJson = "{\"packageClass\":{\"id\":\"" + selected.get(0) + "\"},\"packagePackageProperties\":[{          " +
				"             \"key\": \"Traglast in kg\"," +
				"            \"value\": \"5344\"," +
				"            \"type\": \"number\"}]}";
		
		entity = new HttpEntity<>(requestJson, headers);
		response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(packageItemPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putPackageItemWithPackageClass() throws JsonProcessingException {
		log.info("putPackageItemPackageClass");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packageItemPath = path + "/packageitems/";
		ResponseEntity<PackageItemDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, this.packageItemTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses", String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageClasses").toPrettyString();
		
		List<String> selected = JsonPath.parse(listNode).read("$[*]['id']");
		var testUUID = UUID.randomUUID();
		List<UUID> uuids = new ArrayList<>();
		for (String strUUID : selected) {
			uuids.add(UUID.fromString(strUUID));
		}
		while (uuids.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		dto.setId(null);
		SortedSet<PackagePackagePropertyDTO> packagePackageProperties = new TreeSet<>();
		var property1 = new PackagePackagePropertyDTO();
		property1.setKey("Key");
		property1.setValue("Value");
		property1.setType("Type");
		packagePackageProperties.add(property1);
		this.count += 1;
		
		dto.setPackagePackageProperties(packagePackageProperties);
		dto.setCreateDate(null);
		dto.setModifyDate(null);
		dto.setCreatedBy(null);
		dto.setLastModifiedBy(null);
		dto.setDeleted(false);
		dto.setWeightKg(132423413254132.234532523);
		dto.removeLinks();
		var pC = new PackagePackageClassDTO();
		pC.setId(testUUID);
		dto.setPackageClass(pC);
		
		HttpEntity<PackageItemDTO> entity = new HttpEntity<>(dto, headers);
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PUT, entity, PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		pC = new PackagePackageClassDTO();
		pC.setId(uuids.get(0));
		dto.setPackageClass(pC);
		
		entity = new HttpEntity<>(dto, headers);
		//put
		getRestTemplateGenerator().getTestRestTemplate().exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PUT, entity, PackageItemDTO.class);
		response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + this.packageItemTest.getId(), PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getWeightKg(), response2.getBody().getWeightKg());
		Assertions.assertEquals(dto.getPackageClass().getId(), response2.getBody().getPackageClass().getId());
		Assertions.assertNotNull(response2.getBody().getPackageClass().getId());
		Assertions.assertEquals(this.packageItemTest.getId(), response2.getBody().getId());
		log.info(response2.getBody().getPackagePackageProperties().toString());
		Assertions.assertEquals(1, response2.getBody().getPackagePackageProperties().size());
		
		Assertions.assertEquals(4, this.count);
		for (var property : response2.getBody().getPackagePackageProperties()) {
			if (!property.getKey().equals(property1.getKey())) {
				Assertions.assertTrue(property.isDeleted());
			}
		}
		
	}
	
	@Test
	public void patchPackageItem() throws IOException {
		log.info("patchPackageItem");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var packageItemPath = path + "/packageitems/";
		ResponseEntity<PackageItemDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, this.packageItemTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses", String.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("packageClasses").toPrettyString();
		
		List<String> selected = JsonPath.parse(listNode).read("$[*]['id']");
		var testUUID = UUID.randomUUID();
		List<UUID> uuids = new ArrayList<>();
		for (String strUUID : selected) {
			uuids.add(UUID.fromString(strUUID));
		}
		while (uuids.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		dto.setWeightKg(13242341325.413223);
		var pC = new PackagePackageClassDTO();
		pC.setId(testUUID);
		dto.setPackageClass(pC);
		String request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"}}";
		HttpEntity<String> entity = new HttpEntity<String>(request, headers);
		ResponseEntity<PackageItemDTO> response2 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
		pC = new PackagePackageClassDTO();
		pC.setId(uuids.get(0));
		dto.setPackageClass(pC);
		request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"}}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getWeightKg(), response2.getBody().getWeightKg());
		Assertions.assertEquals(dto.getPackageClass().getId(), response2.getBody().getPackageClass().getId());
		Assertions.assertNotNull(response2.getBody().getPackageClass().getName());
		Assertions.assertEquals(this.packageItemTest.getId(), response2.getBody().getId());
		
		request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"},\n" +
				"\n" +
				"        \"packagePackageProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"5344\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		log.info(response2.getBody().getPackagePackageProperties().toString());
		request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"},\n" +
				"\n" +
				"        \"packagePackageProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"54\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		
		for (var i : response2.getBody().getPackagePackageProperties()) {
			for (var j : response3.getBody().getPackagePackageProperties()) {
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
		request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"},\n" +
				"\n" +
				"        \"packagePackageProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"5344\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"mini\",\n" +
				"            \"value\":\"5\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		response3 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		Assertions.assertEquals(3, response3.getBody().getPackagePackageProperties().size());
		request = "{\"weightKg\":\"" + dto.getWeightKg() + "\",\"packageClass\":{\"id\":\"" + dto.getPackageClass().getId() + "\"},\n" +
				"\n" +
				"        \"packagePackageProperties\": [\n" +
				"            {\"key\":\"groß\",\n" +
				"            \"value\":\"534\",\n" +
				"            \"type\":\"number\"\n" +
				"            },\n" +
				"            {\"key\":\"klein\",\n" +
				"            \"value\":\"54\",\n" +
				"            \"type\":\"number\"\n" +
				"            }\n" +
				"\n" +
				"        ]" +
				"" +
				"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response3 = template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, PackageItemDTO.class);
		Assertions.assertEquals(2, response3.getBody().getPackagePackageProperties().size());
		
		template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "?filter=packagePackageProperties.deleted==all", String.class);
		root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("packageItems");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<PackageItemDTO>>() {
		});
		List<PackageItemDTO> packageItemDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		for (var j : packageItemDTOS) {
			if (j.getId().equals(this.packageItemTest.getId())) {
				for (var i : j.getPackagePackageProperties()) {
					log.info(i.getKey());
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
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
		
		template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "?filter=packagePackageProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("packageItems");
		reader = objectMapper.readerFor(new TypeReference<List<PackageItemDTO>>() {
		});
		packageItemDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : packageItemDTOS) {
			if (j.getId().equals(this.packageItemTest.getId())) {
				for (var i : j.getPackagePackageProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
						}
					} else {
						notdel += 1;
					}
				}
				
			}
		}
		Assertions.assertEquals(1, del);
		Assertions.assertEquals(0, notdel);
		
		template.exchange(packageItemPath + this.packageItemTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(packageItemPath + "?filter=packagePackageProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("packageItems");
		reader = objectMapper.readerFor(new TypeReference<List<PackageItemDTO>>() {
		});
		packageItemDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		for (var j : packageItemDTOS) {
			if (j.getId().equals(this.packageItemTest.getId())) {
				for (var i : j.getPackagePackageProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Glas") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Gefahrengrad")) {
							continue;
						} else {
							del += 1;
							Assertions.assertEquals("mini", i.getKey());
							
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
	public void deletePackageItem() {
		log.info("deletePackageItem");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/packageitems/" + this.packageItemTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<PackageItemDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, this.packageItemTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getPackagePackageProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
}
