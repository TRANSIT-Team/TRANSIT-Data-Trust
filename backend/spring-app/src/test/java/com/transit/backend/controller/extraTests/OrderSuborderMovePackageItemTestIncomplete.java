package com.transit.backend.controller.extraTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.controller.LogFilter;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Import({OrderSuborderController.class})
public class OrderSuborderMovePackageItemTestIncomplete extends BaseIntegrationTest {
	
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
	Set<UUID> packageItemIds;
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
	private UUID thirdPackageItemId;
	
	public OrderSuborderMovePackageItemTestIncomplete(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
	}
	
	@BeforeEach
	void setup() {
		super.updateRestTemplate(false);
		Assertions.assertNotNull(orderController);
		
		Assertions.assertNotNull(orderOrderPropertyController);
		this.count = 0;
		path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.packageItemIds = new HashSet<>();
		this.parentOrderID = setupParentOrder(path).getId();
		this.orderTest = setupOrder(path);
		
		this.path = path + "/orders/" + this.parentOrderID;
		
		
	}
	
	Order setupParentOrder(String path) {
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
		var packageItem3 = this.setupPackage(getBASE_PATH() + getRandomServerPort() + "/api/v1", "Kiste2");
		Set<IdentifierDTO> packageItems = new HashSet<>();
		packageItems.add(new IdentifierDTO(packageItem1.getId()));
		packageItems.add(new IdentifierDTO(packageItem2.getId()));
		packageItems.add(new IdentifierDTO(packageItem3.getId()));
		order.setPackageItemIds(packageItems);
		this.packageItemIds.add(packageItem1.getId());
		this.packageItemIds.add(packageItem2.getId());
		this.thirdPackageItemId = packageItem3.getId();
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
		
		
		Set<IdentifierDTO> packageIds = new HashSet<>();
		for (UUID id : this.packageItemIds) {
			packageIds.add(new IdentifierDTO(id));
		}
		order.setPackageItemIds(packageIds);
		
		order.setCompanyId(new IdentifierDTO(super.getDefaultCompany().getId()));
		
		ResponseEntity<AddressDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order1, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		ResponseEntity<AddressDTO> response11 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order2, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response11.getStatusCode());
		
		order.setAddressIdFrom(new IdentifierDTO(response10.getBody().getId()));
		order.setAddressIdTo(new IdentifierDTO(response11.getBody().getId()));
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
	public void canReadAllPackagesAtRootAndCreateSubOrderAndChangePackageItemPosition() {
		Assertions.assertNotNull(orderController);
		ResponseEntity<OrderDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.parentOrderID, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		var parentPackages = response1.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		var childPackages = response2.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		
		Set<UUID> intersection = new HashSet<>();
		for (var id : parentPackages) {
			if (childPackages.contains(id)) {
				intersection.add(id);
			}
		}
		
		Assertions.assertEquals(3, parentPackages.size());
		Assertions.assertEquals(2, childPackages.size());
		Assertions.assertEquals(2, intersection.size());
	}
	
	@Test
	public void canCreateSecondSubOrder() {
		var order2 = setupOrder2(getBASE_PATH() + getRandomServerPort() + "/api/v1");
		Assertions.assertNotNull(orderController);
		log.info("parentOrder" + this.parentOrderID);
		ResponseEntity<OrderDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.parentOrderID, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		ResponseEntity<OrderDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + order2.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		var parentPackages = response1.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		var childPackages = response2.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		var childPackages2 = response3.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		
		Assertions.assertEquals(3, parentPackages.size());
		Assertions.assertEquals(2, childPackages.size());
		Assertions.assertEquals(1, childPackages2.size());
		
		
	}
	
	Order setupOrder2(String path) {
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
		
		
		Set<IdentifierDTO> packageIds = new HashSet<>();
		packageIds.add(new IdentifierDTO(thirdPackageItemId));
		order.setPackageItemIds(packageIds);
		
		order.setCompanyId(new IdentifierDTO(super.getDefaultCompany().getId()));
		
		ResponseEntity<AddressDTO> response10 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order1, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		ResponseEntity<AddressDTO> response11 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/addresses", order2, AddressDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response11.getStatusCode());
		
		order.setAddressIdFrom(new IdentifierDTO(response10.getBody().getId()));
		order.setAddressIdTo(new IdentifierDTO(response11.getBody().getId()));
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
	
	@Test
	public void canCreateSecondSubOrderANdDeleteFirst() {
		var order2 = setupOrder2(getBASE_PATH() + getRandomServerPort() + "/api/v1");
		Assertions.assertNotNull(orderController);
		ResponseEntity<OrderDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.parentOrderID, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
		ResponseEntity<OrderDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		ResponseEntity<OrderDTO> response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + order2.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		var parentPackages = response1.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		var childPackages = response2.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		var childPackages2 = response3.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		
		Assertions.assertEquals(3, parentPackages.size());
		Assertions.assertEquals(2, childPackages.size());
		Assertions.assertEquals(1, childPackages2.size());
		
		getRestTemplateGenerator().getTestRestTemplate().delete(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId(), OrderDTO.class);
		
		response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.parentOrderID, OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
		response3 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + order2.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		parentPackages = response1.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		childPackages2 = response3.getBody().getPackageItemIds().stream().map(IdentifierDTO::getId).collect(Collectors.toSet());
		
		Assertions.assertEquals(3, parentPackages.size());
		
		Assertions.assertEquals(1, childPackages2.size());
		
		response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1" + "/orders/" + this.orderTest.getId(), OrderDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
		Assertions.assertEquals(2, response2.getBody().getPackageItemIds().size());
	}
	
}