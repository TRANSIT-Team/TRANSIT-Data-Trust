package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.OrderOrderPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderOrderPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.service.mapper.OrderPropertyMapper;
import com.transit.backend.datalayers.service.mapper.PackageItemMapper;
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

import java.util.*;

@Slf4j
@Import(OrderOrderPropertyController.class)
public class OrderOrderPropertyControllerTest extends BaseIntegrationTest {
	
	
	@InjectMocks
	OrderOrderPropertyController orderPropertiesController;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackageItemMapper packageItemMapper;
	@Autowired
	private OrderPropertyMapper orderPropertiesMapper;
	@Autowired
	private OrderOrderPropertyAssemblerWrapper orderPropertiesAssembler;
	private OrderProperty orderPropertiesTest;
	private String path;
	private UUID orderItemId;
	private GeometryFactory geoFactory;
	
	public OrderOrderPropertyControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(orderPropertiesController);
	}
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(orderPropertiesController);
		
		
		this.orderPropertiesTest = setupOrderProperty();
		
		
	}
	
	OrderProperty setupOrderProperty() throws JsonProcessingException {
		
		this.geoFactory = new GeometryFactory();
		
		var order = new OrderDTO();
		order.setOrderStatus(OrderStatus.OPEN.name());
		
		var order1 = new AddressDTO();
		this.geoFactory = new GeometryFactory();
		order1.setLocationPoint(geoFactory.createPoint(new Coordinate(20.34132, 53.4354)));
		order1.setCity("TestStadt");
		order1.setState("TestState");
		order1.setCountry("TestLand");
		order1.setStreet("Teststrasse");
		order1.setIsoCode("DE");
		order1.setZip("11111");
		order1.setAddressExtra("50. Etage");
		var order2 = new AddressDTO();
		order2.setLocationPoint(geoFactory.createPoint(new Coordinate(10.43563457635768, 50.435345345)));
		order2.setCity("Berlin");
		order2.setState("Berlin");
		order2.setCountry("Deutschland");
		order2.setStreet("Pariser Platz");
		order2.setIsoCode("DE");
		order2.setZip("10117");
		order2.setAddressExtra("0. Etage");
		
		SortedSet<OrderPropertyDTO> orderProperties = new TreeSet<>();
		var orderProperties1 = new OrderPropertyDTO();
		orderProperties1.setKey("Time WIndows");
		orderProperties1.setValue("9:00-10:00");
		orderProperties1.setType("string");
		String path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		
		
		var packageItem1 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1");
		var packageItem2 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1", "Kiste");
		
		Set<IdentifierDTO> packageItemIds = new HashSet<>();
		packageItemIds.add(new IdentifierDTO(packageItem1.getId()));
		packageItemIds.add(new IdentifierDTO(packageItem2.getId()));
		order.setPackageItemIds(packageItemIds);
		
		order.setCompanyId(new IdentifierDTO(super.getDefaultCompany().getId()));
		
		ResponseEntity<AddressDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order1, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		ResponseEntity<AddressDTO> response11 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order2, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response11.getStatusCode());
		
		order.setAddressIdFrom(new IdentifierDTO(response10.getBody().getId()));
		order.setAddressIdTo(new IdentifierDTO(response11.getBody().getId()));
		order.setAddressIdBilling(new IdentifierDTO(response11.getBody().getId()));
		
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders", order, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		
		this.orderItemId = response2.getBody().getId();
		
		ResponseEntity<OrderPropertyDTO> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties1, OrderPropertyDTO.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.path = path + "/orders/" + response2.getBody().getId();
		return orderPropertiesMapper.toEntity(response6.getBody());
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
		
		ResponseEntity<PackagePackageProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties2, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		
		ResponseEntity<PackagePackageProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties3, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		
		
		ResponseEntity<PackageItemDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, response2.getBody().getId());
		return packageItemMapper.toEntity(response9.getBody());
	}
	
	@Test
	public void getReturnsCorrectResponse() throws Exception {
		
		var orderPropertiesPath = this.path + "/orderproperties/";
		Assertions.assertNotNull(this.orderPropertiesTest);
		Assertions.assertNotNull(this.orderPropertiesTest.getId());
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPropertiesPath + "{id}", String.class, this.orderPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(response.getBody());
		log.info(response.getBody());
		verifyJsonOrderNotNullValues(response);
		
	}
	
	
	void verifyJsonOrderNotNullValues(final ResponseEntity<String> response) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var orderPropertiesDTO = orderPropertiesAssembler.toModel(this.orderPropertiesTest, this.orderItemId, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.orderPropertiesTest.getId(), this.orderPropertiesTest.getCreateDate(), this.orderPropertiesTest.getModifyDate(), this.orderPropertiesTest.isDeleted());
		
		Assertions.assertEquals(this.orderPropertiesTest.getKey(), root.get("key").asText());
		Assertions.assertEquals(this.orderPropertiesTest.getValue(), root.get("value").asText());
		Assertions.assertEquals(this.orderPropertiesTest.getType(), root.get("type").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + orderPropertiesDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		var orderPropertiesPath = this.path + "/orderproperties/";
		var orderProperties = new OrderPropertyDTO();
		orderProperties.setKey("Gefahrengrad");
		orderProperties.setValue("F");
		orderProperties.setType("string");
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(this.path + "/orderproperties", orderProperties, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/orderproperties", String.class);
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("orderProperties").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		
		Assertions.assertTrue(selected.size() >= 2);
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<OrderPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPropertiesPath + "{id}", OrderPropertyDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		Assertions.assertNotNull(response2.getBody());
		
	}
	
	@Test
	public void postEmptyOrderProperties() {
		log.info("postEmptyOrderProperties");
		var orderPropertiesPath = this.path + "/orderproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(orderPropertiesPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
	}
	
	@Test
	public void postOrderProperties() {
		log.info("postOrderPropertiesNotFound");
		var orderPropertiesPath = this.path + "/orderproperties/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		OrderProperty orderProperties = new OrderProperty();
		orderProperties.setKey("Zerbrechlich");
		orderProperties.setValue("true");
		orderProperties.setType("boolean");
		
		HttpEntity<OrderPropertyDTO> entity = new HttpEntity<>(orderPropertiesMapper.toDto(orderProperties), headers);
		ResponseEntity<OrderPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(orderPropertiesPath, entity, OrderPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putOrderProperties() {
		log.info("putOrderProperties");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var orderPropertiesPath = this.path + "/orderproperties/";
		ResponseEntity<OrderPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPropertiesPath + "{id}", OrderPropertyDTO.class, this.orderPropertiesTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var dtoUpdate = new OrderProperty();
		dtoUpdate.setKey(dto.getKey());
		dtoUpdate.setValue("5");
		dto.setValue(dtoUpdate.getValue());
		dtoUpdate.setType(dto.getValue());
		
		HttpEntity<OrderPropertyDTO> entity = new HttpEntity<>(orderPropertiesMapper.toDto(dtoUpdate), headers);
		ResponseEntity<OrderPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(orderPropertiesPath + this.orderPropertiesTest.getId(), HttpMethod.PUT, entity, OrderPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.orderPropertiesTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getValue(), response2.getBody().getValue());
	}
	
	@Test
	public void patchOrderProperties() {
		log.info("patchOrderProperties");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var orderPropertiesPath = this.path + "/orderproperties/";
		
		
		String request = "{\"value\":\"4\"}";
		var entity = new HttpEntity<>(request, headers);
		
		var response = template.exchange(orderPropertiesPath + this.orderPropertiesTest.getId(), HttpMethod.PATCH, entity, OrderPropertyDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("4", response.getBody().getValue());
		ResponseEntity<OrderPropertyDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/orderproperties/{id}", OrderPropertyDTO.class, this.orderPropertiesTest.getId());
		this.orderPropertiesTest = orderPropertiesMapper.toEntity(response2.getBody());
		
		Assertions.assertEquals(this.orderPropertiesTest.getId(), response.getBody().getId());
		Assertions.assertEquals(this.orderPropertiesTest.getValue(), response.getBody().getValue());
	}
	
	@Test
	public void deleteOrderProperties() {
		log.info("deleteOrder");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(this.path + "/orderproperties/" + this.orderPropertiesTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		//	ResponseEntity<OrderPropertyDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "/orderproperties/{id}", OrderPropertyDTO.class, this.orderPropertiesTest.getId());
		//	Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		//	Assertions.assertTrue(response3.getBody().isDeleted());
	}
	
	
}

