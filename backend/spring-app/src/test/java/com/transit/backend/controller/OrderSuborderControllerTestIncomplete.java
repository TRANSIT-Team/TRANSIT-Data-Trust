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
import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.OrderOrderPropertyController;
import com.transit.backend.datalayers.controller.OrderSuborderController;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderSuborderAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.datalayers.service.mapper.PackageItemMapper;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

@Slf4j
@Import({OrderSuborderController.class})
public class OrderSuborderControllerTestIncomplete extends BaseIntegrationTest {
	
	@InjectMocks
	OrderController parentOrderController;
	@InjectMocks
	OrderSuborderController orderController;
	@InjectMocks
	OrderOrderPropertyController orderOrderPropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@Autowired
	ObjectMapper objectMapper;
	Set<IdentifierDTO> packageItemIdParent;
	@Autowired
	private PackageItemMapper packageItemMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderSuborderAssemblerWrapper orderAssembler;
	private Order orderTest;
	private String path;
	private UUID parentOrderID;
	private int count;
	private GeometryFactory geoFactory;
	
	public OrderSuborderControllerTestIncomplete(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	@Test
	public void contextLoads() {
		Assertions.assertNotNull(orderController);
	}
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(orderController);
		
		Assertions.assertNotNull(orderOrderPropertyController);
		this.count = 0;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.parentOrderID = setupParentOrder(path).getId();
		
		this.orderTest = setupOrder(path);
		
		this.path = path + "/orders/" + this.parentOrderID;
		
		
	}
	
	Order setupParentOrder(String path) {
		this.packageItemIdParent = new HashSet<>();
		this.geoFactory = new GeometryFactory();
		
		var order = new OrderDTO();
		order.setOrderStatus(OrderStatus.OPEN.name());
		
		var order1 = new AddressDTO();
		this.geoFactory = new GeometryFactory();
		order1.setLocationPoint(geoFactory.createPoint(new Coordinate(53.4354, 20.34132)));
		order1.setCity("TestStadt");
		order1.setState("TestState");
		order1.setCountry("TestLand");
		order1.setStreet("Teststrasse");
		order1.setIsoCode("DE");
		order1.setZip("11111");
		order1.setAddressExtra("50. Etage");
		var order2 = new AddressDTO();
		order2.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435345345, 10.43563457635768)));
		order2.setCity("TestStadt2");
		order2.setState("TestState2");
		order2.setCountry("TestLand2");
		order2.setStreet("Teststrasse2");
		order2.setIsoCode("DE");
		order2.setZip("11121");
		order2.setAddressExtra("10. Etage");
		
		SortedSet<OrderPropertyDTO> orderProperties = new TreeSet<>();
		var orderProperties1 = new OrderPropertyDTO();
		orderProperties1.setKey("Glas");
		orderProperties1.setValue("Vorsicht zerbrechlich");
		orderProperties1.setType("string");
		
		var orderProperties2 = new OrderPropertyDTO();
		orderProperties2.setKey("Gefahrgutklasse");
		orderProperties2.setValue("3");
		orderProperties2.setType("int");
		
		var orderProperties3 = new OrderPropertyDTO();
		orderProperties3.setKey("Gefahrengrad");
		orderProperties3.setValue("F");
		orderProperties3.setType("char");
		
		var packageItem1 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1");
		var packageItem2 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1", "Kiste");
		
		Set<IdentifierDTO> packageItemIds = new HashSet<>();
		packageItemIds.add(new IdentifierDTO(packageItem1.getId()));
		packageItemIds.add(new IdentifierDTO(packageItem2.getId()));
		packageItemIdParent.add(new IdentifierDTO(packageItem1.getId()));
		packageItemIdParent.add(new IdentifierDTO(packageItem2.getId()));
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
		
		ResponseEntity<OrderProperty> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties1, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.count += 1;
		ResponseEntity<OrderProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties2, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		this.count += 1;
		ResponseEntity<OrderProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties3, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		this.count += 1;
		
		ResponseEntity<OrderDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/orders/{id}", OrderDTO.class, response2.getBody().getId());
		log.info(getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/orders/{id}", String.class, response2.getBody().getId()).getBody());
		return orderMapper.toEntity(response9.getBody());
	}
	
	Order setupOrder(String path) {
		this.geoFactory = new GeometryFactory();
		
		var order = new OrderDTO();
		order.setOrderStatus(OrderStatus.OPEN.name());
		
		var order1 = new AddressDTO();
		this.geoFactory = new GeometryFactory();
		order1.setLocationPoint(geoFactory.createPoint(new Coordinate(53.4354, 20.34132)));
		order1.setCity("TestStadt");
		order1.setState("TestState");
		order1.setCountry("TestLand");
		order1.setStreet("Teststrasse");
		order1.setIsoCode("DE");
		order1.setZip("11111");
		order1.setAddressExtra("50. Etage");
		var order2 = new AddressDTO();
		order2.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435345345, 10.43563457635768)));
		order2.setCity("TestStadt2");
		order2.setState("TestState2");
		order2.setCountry("TestLand2");
		order2.setStreet("Teststrasse2");
		order2.setIsoCode("DE");
		order2.setZip("11121");
		order2.setAddressExtra("10. Etage");
		
		SortedSet<OrderPropertyDTO> orderProperties = new TreeSet<>();
		var orderProperties1 = new OrderPropertyDTO();
		orderProperties1.setKey("Glas");
		orderProperties1.setValue("Vorsicht zerbrechlich");
		orderProperties1.setType("string");
		
		var orderProperties2 = new OrderPropertyDTO();
		orderProperties2.setKey("Gefahrgutklasse");
		orderProperties2.setValue("3");
		orderProperties2.setType("int");
		
		var orderProperties3 = new OrderPropertyDTO();
		orderProperties3.setKey("Gefahrengrad");
		orderProperties3.setValue("F");
		orderProperties3.setType("char");
		Assertions.assertEquals(2, packageItemIdParent.size());
		Set<IdentifierDTO> packageItemIds = new HashSet<>(packageItemIdParent);
		order.setPackageItemIds(packageItemIds);
		
		order.setCompanyId(new IdentifierDTO(super.getDefaultCompany().getId()));
		
		ResponseEntity<AddressDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order1, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		ResponseEntity<AddressDTO> response11 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order2, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response11.getStatusCode());
		
		order.setAddressIdFrom(new IdentifierDTO(Objects.requireNonNull(response10.getBody()).getId()));
		order.setAddressIdTo(new IdentifierDTO(Objects.requireNonNull(response11.getBody()).getId()));
		order.setAddressIdBilling(new IdentifierDTO(response11.getBody().getId()));
		
		
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/" + this.parentOrderID + "/suborders", order, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		
		ResponseEntity<OrderProperty> response6 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties1, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response6.getStatusCode());
		this.count += 1;
		ResponseEntity<OrderProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties2, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		this.count += 1;
		ResponseEntity<OrderProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/orders/{id}/orderproperties", orderProperties3, OrderProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		this.count += 1;
		
		ResponseEntity<OrderDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/orders/{id}", OrderDTO.class, response2.getBody().getId());
		log.info(getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/orders/{id}", String.class, response2.getBody().getId()).getBody());
		return orderMapper.toEntity(response9.getBody());
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
		
		ResponseEntity<PackagePackageProperty> response7 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties2, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response7.getStatusCode());
		
		ResponseEntity<PackagePackageProperty> response8 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems/{id}/packagepackageproperties", packageItemPackageProperties3, PackagePackageProperty.class, response2.getBody().getId());
		Assertions.assertEquals(HttpStatus.CREATED, response8.getStatusCode());
		
		
		ResponseEntity<PackageItemDTO> response9 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageitems/{id}", PackageItemDTO.class, response2.getBody().getId());
		return packageItemMapper.toEntity(response9.getBody());
	}
	
	@Test
	public void getReturnsCorrectResponseOrder() throws Exception {
		
		var orderPath = path + "/suborders/";
		Assertions.assertNotNull(this.orderTest);
		
		
		Assertions.assertEquals(this.orderTest, orderMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "{id}", OrderDTO.class, this.orderTest.getId()).getBody()));
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "{id}", String.class, this.orderTest.getId());
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "{id}", OrderDTO.class, this.orderTest.getId());
		log.info("roundedTime" + response2.getBody().getCreateDate().toString());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonOrderNotNullValuesOrder(path, response, response2);
		
	}
	
	
	void verifyJsonOrderNotNullValuesOrder(String path, final ResponseEntity<String> response, ResponseEntity<OrderDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var orderDTO = orderAssembler.toModel(this.orderTest, this.parentOrderID, true);
		Assertions.assertNotNull(root.asText());
		verifyJsonAbstractParameter(root, this.orderTest.getId(), this.orderTest.getCreateDate(), this.orderTest.getModifyDate(), this.orderTest.isDeleted());
		
		Assertions.assertEquals(this.orderTest.getOrderStatus().name(), root.get("orderStatus").asText());
		Assertions.assertEquals(this.orderTest.getAddressFrom().getId(), UUID.fromString(root.get("addressIdFrom").get("id").asText()));
		Assertions.assertEquals(this.orderTest.getAddressTo().getId(), UUID.fromString(root.get("addressIdTo").get("id").asText()));
		Assertions.assertEquals(this.orderTest.getAddressBilling().getId(), UUID.fromString(root.get("addressIdBilling").get("id").asText()));
		JsonNode self = root.get("_links").get("self");
		;
		Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + orderDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
		
		verifyJsonOrderPropertiesNotNullValues(path, root.get("orderProperties"), orderDTO);
		
	}
	
	void verifyJsonOrderPropertiesNotNullValues(String path, JsonNode node, OrderDTO orderDTO) throws Exception {
		
		var orderPropertiesIterator = this.orderTest.getOrderProperties().iterator();
		for (int z = 0; z < this.orderTest.getOrderProperties().size(); z++) {
			OrderProperty orderProperty = orderPropertiesIterator.next();
			OrderPropertyDTO orderPropertyDTO = orderDTO.getOrderProperties().stream().filter(orderPropertiesDTO1 -> orderPropertiesDTO1.getId().equals(orderProperty.getId())).collect(Collectors.toList()).get(0);
			Filter getOrderPropertiesById = filter(
					where("id").is(orderProperty.getId().toString())
			);
			
			JSONArray selected = JsonPath.parse(node.toPrettyString()).read("$.[?]", getOrderPropertiesById);
			JsonNode nodeOrderProperties = objectMapper.readTree(selected.toJSONString()).get(0);
			verifyJsonAbstractParameter(nodeOrderProperties, orderProperty.getId(), orderProperty.getCreateDate(), orderProperty.getModifyDate(), orderProperty.isDeleted());
			Assertions.assertEquals(orderProperty.getKey(), nodeOrderProperties.get("key").asText());
			Assertions.assertEquals(orderProperty.getValue(), nodeOrderProperties.get("value").asText());
			Assertions.assertEquals(orderProperty.getType(), nodeOrderProperties.get("type").asText());
			JsonNode self = nodeOrderProperties.get("_links").get("self");
			Assertions.assertEquals(Link.of(getBASE_PATH() + getRandomServerPort() + "/api/v1" + orderPropertyDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
			
		}
	}
	
	
	@Test
	public void getReturnsNotFound() throws Exception {
		setupOrder(getBASE_PATH() + getRandomServerPort() + "/api/v1");
		var orderPath = path + "/suborders/";
		
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath, String.class, this.orderTest.getId());
		JsonNode root = objectMapper.readTree(response.getBody());
		String listNode = root.get("_embedded").get("orders").toPrettyString();
		
		List<UUID> selected = JsonPath.parse(listNode).read("$[*]['id']");
		Assertions.assertTrue(selected.size() >= 2);
		Assertions.assertNotNull(root.get("_links"));
		var testUUID = UUID.randomUUID();
		
		while (selected.contains(testUUID)) {
			testUUID = UUID.randomUUID();
		}
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "{id}", OrderDTO.class, testUUID);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
		
	}
	
	@Test
	public void postEmptyOrder() throws JsonProcessingException {
		log.info("postEmptyOrder");
		var orderPath = path + "/suborders/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{}";
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(orderPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		
		requestJson = "{\"companyId\":{\"id\":\"" + super.getDefaultCompany().getId() + "\"}}";
		entity = new HttpEntity<String>(requestJson, headers);
		response = getRestTemplateGenerator().getTestRestTemplate().postForEntity(orderPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		
	}
	
	@Test
	public void postOrder() throws JsonProcessingException {
		log.info("postOrder");
		var orderPath = path + "/suborders/";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var packageItem1 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1", "Kiste2");
		var packageItem2 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1", "Kiste3");
		
		String requestJson;
		HttpEntity<String> entity;
		ResponseEntity<String> response2;
		requestJson = "      { \"addressIdFrom\": {\"id\": \"" + this.orderTest.getAddressTo().getId() + "\" }," +
				"\"addressIdTo\": {	\"id\": \"" + this.orderTest.getAddressFrom().getId() + "\"	}," +
				"\"addressIdBilling\": {		\"id\": \"" + this.orderTest.getAddressBilling().getId() + "\"	}," +
				"\"companyId\": {\"id\": \"" + this.orderTest.getCompany().getId() + "\"}," +
				"\"orderStatus\": \"OPEN\"," +
				"\"packageItemIds\": [	{\"id\": \"" + packageItem1.getId() + "\"	}," +
				"{\"id\": \"" + packageItem2.getId() + "\"}         ]," +
				
				"\"orderProperties\": [	{" +
				"\"key\": \"Time WIndow Delivery\"," +
				"\"value\": \"10-11 Uhr\"," +
				"\"type\": \"text\"} ]" +
				"}";
		
		entity = new HttpEntity<>(requestJson, headers);
		response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(orderPath, entity, String.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
	}
	
	@Test
	public void putOrder() throws JsonProcessingException {
		log.info("putOrder");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		var orderPath = path + "/suborders/";
		ResponseEntity<OrderDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "{id}", OrderDTO.class, this.orderTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		var update = new Order();
		// orderMapper.toEntity(dto);
		
		update.setOrderStatus(OrderStatus.COMPLETE);
		assert dto != null;
		dto.setOrderStatus(OrderStatus.COMPLETE.name());
		
		HttpEntity<OrderDTO> entity = new HttpEntity<>(orderMapper.toDto(update), headers);
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(orderPath + this.orderTest.getId(), HttpMethod.PUT, entity, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response2.getStatusCode());
		update = orderMapper.toEntity(dto);
		update.setId(null);
		update.setOrderStatus(OrderStatus.OPEN);
		update.setCreateDate(null);
		update.setModifyDate(null);
		update.setCreatedBy(null);
		update.setLastModifiedBy(null);
		update.setOrderProperties(update.getOrderProperties().stream().map(oP -> {
			oP.setCreateDate(null);
			oP.setModifyDate(null);
			oP.setCreatedBy(null);
			oP.setLastModifiedBy(null);
			oP.setId(null);
			return oP;
		}).collect(Collectors.toCollection(TreeSet::new)));
		var dtonew = orderMapper.toDto(update);
		// while incomplete
		dtonew.setOrderRouteIds(null);
		dtonew.setOrderTypeIds(null);
		dtonew.setSuborderIds(null);
		
		log.error("test-out");
		log.error(dtonew.getAddressIdFrom().getId().toString());
		log.error(dtonew.getAddressIdTo().getId().toString());
		
		
		dto.setOrderStatus(OrderStatus.OPEN.name());
		;
		entity = new HttpEntity<>(dtonew, headers);
		response2 = getRestTemplateGenerator().getTestRestTemplate().exchange(orderPath + this.orderTest.getId(), HttpMethod.PUT, entity, OrderDTO.class);
		
		
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(this.orderTest.getId(), response2.getBody().getId());
		Assertions.assertEquals(dto.getOrderStatus(), response2.getBody().getOrderStatus());
		Assertions.assertNotNull(response2.getBody().getCreatedBy());
		
		dto.setOrderStatus(OrderStatus.PROCESSING.name());
		
		OrderStatusDTO dtoNew = new OrderStatusDTO(OrderStatus.PROCESSING.name());
		
		var entityNew = new HttpEntity<>(dtoNew, headers);
		var response4 = getRestTemplateGenerator().getTestRestTemplate().exchange(
				getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId() + "/orderstatus",
				
				HttpMethod.POST, entityNew, OrderStatusDTO.class);
		
		
		Assertions.assertEquals(HttpStatus.OK, response4.getStatusCode());
		Assertions.assertEquals(dto.getOrderStatus(), Objects.requireNonNull(response4.getBody()).getOrderStatus());
	}
	
	@Test
	public void patchOrder() throws IOException {
		log.info("patchOrder");
		var template = getRestTemplateGenerator().getTestRestTemplate().getRestTemplate();
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/merge-patch+json");
		
		var orderPath = path + "/suborders/";
		ResponseEntity<OrderDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/suborders/{id}", OrderDTO.class, this.orderTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		var dto = response3.getBody();
		
		HttpEntity<String> entity;
		ResponseEntity<OrderDTO> response2;
		
		String request = "{\"orderStatus\":\"" + dto.getOrderStatus() + "\"}";
		entity = new HttpEntity<>(request, headers);
		//patch
		
		response2 = template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(dto.getOrderStatus(), response2.getBody().getOrderStatus());
		Assertions.assertNotNull(response2.getBody().getOrderStatus());
		Assertions.assertEquals(this.orderTest.getId(), response2.getBody().getId());
		
		request = "{\"orderStatus\":\"" + dto.getOrderStatus() + "\"," +
				"        \"orderProperties\": [\n" +
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
		
		response2 = template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, OrderDTO.class);
		log.info(response2.getBody().getOrderProperties().toString());
		request = "{\"orderStatus\":\"" + dto.getOrderStatus() + "\"," +
				"        \"orderProperties\": [\n" +
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
		
		response3 = template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, OrderDTO.class);
		
		for (var i : response2.getBody().getOrderProperties()) {
			for (var j : response3.getBody().getOrderProperties()) {
				if (Objects.equals(i.getKey(), j.getKey())) {
					Assertions.assertEquals(i.getId(), j.getId());
					Assertions.assertEquals(i.getType(), j.getType());
					Assertions.assertEquals(i.isDeleted(), j.isDeleted());
					Assertions.assertEquals(i.getCreateDate().truncatedTo(ChronoUnit.MILLIS), j.getCreateDate().truncatedTo(ChronoUnit.MILLIS));
					Assertions.assertNotEquals(i.getValue(), j.getValue());
					Assertions.assertNotEquals(i.getModifyDate(), j.getModifyDate());
				}
			}
		}
		
		
		request = "{\"orderStatus\":\"" + dto.getOrderStatus() + "\"," +
				"        \"orderProperties\": [\n" +
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
		response3 = template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, OrderDTO.class);
		Assertions.assertEquals(3, response3.getBody().getOrderProperties().size());
		request = "{\"orderStatus\":\"" + dto.getOrderStatus() + "\"," +
				"        \"orderProperties\": [\n" +
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
		
		response3 = template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, OrderDTO.class);
		Assertions.assertEquals(2, response3.getBody().getOrderProperties().size());
		
		template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, String.class);
		ResponseEntity<String> response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "?filter=orderProperties.deleted==all", String.class);
		var root = objectMapper.readTree(response4.getBody());
		JsonNode node = root.get("_embedded").get("orders");
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<OrderDTO>>() {
		});
		List<OrderDTO> orderDTOS = reader.readValue(node);
		var del = 0;
		var notdel = 0;
		
		for (var j : orderDTOS) {
			if (j.getId().equals(this.orderTest.getId())) {
				for (var i : j.getOrderProperties()) {
					log.info(i.getKey());
					if (i.isDeleted()) {
						if (i.getKey().equals("Gefahrengrad") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Glas")) {
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
		
		template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "?filter=orderProperties.deleted==true", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("orders");
		reader = objectMapper.readerFor(new TypeReference<List<OrderDTO>>() {
		});
		orderDTOS = reader.readValue(node);
		log.info(response4.getBody());
		log.info(this.orderTest.getId().toString());
		del = 0;
		notdel = 0;
		for (var j : orderDTOS) {
			if (j.getId().equals(this.orderTest.getId())) {
				for (var i : j.getOrderProperties()) {
					log.info(i.getKey());
					
					if (i.isDeleted()) {
						if (i.getKey().equals("Gefahrengrad") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Glas")) {
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
		
		template.exchange(orderPath + this.orderTest.getId(), HttpMethod.PATCH, entity, String.class);
		response4 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(orderPath + "?filter=orderProperties.deleted==false", String.class);
		root = objectMapper.readTree(response4.getBody());
		node = root.get("_embedded").get("orders");
		reader = objectMapper.readerFor(new TypeReference<List<OrderDTO>>() {
		});
		orderDTOS = reader.readValue(node);
		del = 0;
		notdel = 0;
		
		for (var j : orderDTOS) {
			if (j.getId().equals(this.orderTest.getId())) {
				for (var i : j.getOrderProperties()) {
					if (i.isDeleted()) {
						if (i.getKey().equals("Gefahrengrad") || i.getKey().equals("Gefahrgutklasse") || i.getKey().equals("Glas")) {
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
	public void deleteOrder() {
		log.info("deleteOrder");
		ResponseEntity<Void> response = getRestTemplateGenerator().getTestRestTemplate().exchange(path + "/suborders/" + this.orderTest.getId(), HttpMethod.DELETE, null, Void.class);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		ResponseEntity<OrderDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/suborders/{id}", OrderDTO.class, this.orderTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response3.getStatusCode());
		Assertions.assertTrue(response3.getBody().isDeleted());
		for (var property : response3.getBody().getOrderProperties()) {
			Assertions.assertTrue(property.isDeleted());
		}
	}
	
	
}
