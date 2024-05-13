package com.transit.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.BaseIntegrationTest;
import com.transit.backend.RestTemplateGenerator;
import com.transit.backend.datalayers.controller.PackageItemController;
import com.transit.backend.datalayers.controller.PackageItemPackageClassController;
import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.PackageClassAssembler;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.service.mapper.PackageClassMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Slf4j
@Import(PackageItemPackageClassController.class)
public class PackageItemPackageClassControllerTest extends BaseIntegrationTest {
	
	
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
	private PackageClassMapper packageClassMapper;
	
	@Autowired
	private PackageClassAssembler packageClassAssembler;
	private PackageClass packageClassTest;
	
	private String path;
	
	public PackageItemPackageClassControllerTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	
	@Test
	public void contextLoads() throws Exception {
		Assertions.assertNotNull(packageItemController);
		Assertions.assertNotNull(packageClassController);
	}
	
	
	@BeforeEach
	void setup() throws JsonProcessingException {
		super.updateRestTemplate(false);
/*		setMockMvc(MockMvcBuilders.standaloneSetup(this.controller)
				//MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilter(logFilter, "/*")
				.build());
*/
		Assertions.assertNotNull(packageItemController);
		Assertions.assertNotNull(packageClassController);
		Assertions.assertNotNull(packageItemPackagePackagePropertyController);
		this.path = getBASE_PATH() + getRandomServerPort() + "/api/v1";
		this.packageClassTest = setupPackage(path);
		
		
	}
	
	PackageClass setupPackage(String path) throws JsonProcessingException {
		
		
		var packageItem = new PackageItem();
		packageItem.setHeightCm(12);
		packageItem.setWeightKg(12341324);
		packageItem.setDeepCm(4234);
		packageItem.setWidthCm(32532532);
		
		
		var packageProperties1 = new PackageProperty();
		packageProperties1.setKey("Glas");
		packageProperties1.setDefaultValue("Vorsicht zerbrechlich");
		packageProperties1.setType("string");
		
		var packageProperties2 = new PackageProperty();
		packageProperties2.setKey("Gefahrgutklasse");
		packageProperties2.setDefaultValue("3");
		packageProperties2.setType("int");
		
		var packageProperties3 = new PackageProperty();
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
		
		
		var packageClass = new PackageClass();
		packageClass.setName("Pappkarton_gepolstert");
		
		ResponseEntity<PackageClassDTO> response1 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageclasses", packageClass, PackageClassDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		response1 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(path + "/packageclasses/" + response1.getBody().getId(), PackageClassDTO.class);
		var packageClassAdd = new PackageClass();
		packageClassAdd.setId(response1.getBody().getId());
		packageItem.setPackageClass(packageClassAdd);
		
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().postForEntity(path + "/packageitems", packageItem, PackageItemDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		log.info(response2.getBody().getId().toString());
		log.info(response2.getBody().getPackageClass().getId().toString());
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
		this.path = this.path + "/packageitems/" + response2.getBody().getId() + "/packageclasses/";
		
		Assertions.assertEquals(response1.getBody().getId(), response9.getBody().getPackageClass().getId());
		Assertions.assertEquals(response1.getBody().getName(), response9.getBody().getPackageClass().getName());
		return packageClassMapper.toEntity(response1.getBody());
	}
	
	
	@Test
	public void getReturnsCorrectResponsePackageItem() throws Exception {
		
		
		Assertions.assertNotNull(this.packageClassTest);
		Assertions.assertEquals(this.packageClassTest.getId(), packageClassMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "{id}", PackageClassDTO.class, this.packageClassTest.getId()).getBody()).getId());
		
		Assertions.assertEquals(this.packageClassTest.getId(), packageClassMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "{id}", PackageClassDTO.class, this.packageClassTest.getId()).getBody()).getId());
		
		Assertions.assertEquals(this.packageClassTest.getName(), packageClassMapper.toEntity(getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "{id}", PackageClassDTO.class, this.packageClassTest.getId()).getBody()).getName());
		ResponseEntity<String> response = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "{id}", String.class, this.packageClassTest.getId());
		ResponseEntity<PackageItemDTO> response2 = getRestTemplateGenerator().getTestRestTemplate().getForEntity(this.path + "{id}", PackageItemDTO.class, this.packageClassTest.getId());
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Assertions.assertNotNull(response.getBody());
		verifyJsonPackageNotNullValuesPackageItem(path, response, response2);
		
	}
	
	void verifyJsonPackageNotNullValuesPackageItem(String path, final ResponseEntity<String> response, ResponseEntity<PackageItemDTO> response2) throws Exception {
		
		JsonNode root = objectMapper.readTree(response.getBody());
		var packageItemDTO = packageClassAssembler.toModel(this.packageClassTest);
		Assertions.assertNotNull(root.asText());
		log.info(root.toPrettyString());
		verifyJsonAbstractParameter(root, this.packageClassTest.getId(), this.packageClassTest.getCreateDate(), this.packageClassTest.getModifyDate(), this.packageClassTest.isDeleted());
		Assertions.assertEquals(this.packageClassTest.getId(), UUID.fromString(root.get("id").asText()));
		Assertions.assertEquals(this.packageClassTest.getName(), root.get("name").asText());
		JsonNode self = root.get("_links").get("self");
		Assertions.assertEquals(Link.of(this.path.substring(0, this.path.length() - 16) + packageItemDTO.getLink("self").get().getHref()), Link.of(self.get("href").asText()));
		
	}
}