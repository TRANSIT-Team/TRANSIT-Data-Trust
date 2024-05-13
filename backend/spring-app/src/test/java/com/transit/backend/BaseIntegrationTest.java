package com.transit.backend;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.controller.LogFilter;
import com.transit.backend.datalayers.controller.*;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.controller.dto.registration.CompanyAddressDTORegistration;
import com.transit.backend.datalayers.controller.dto.registration.CompanyDTORegistration;
import com.transit.backend.datalayers.controller.dto.registration.UserDTORegistration;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.repository.PackageItemRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.datalayers.service.PackageItemService;
import com.transit.backend.datalayers.service.helper.KeycloakRolesManagement;
import com.transit.backend.datalayers.service.impl.UserServiceBean;
import com.transit.backend.datalayers.service.mapper.CompanyMapper;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		classes = {BackendApplication.class, PackageItemController.class, PackageItemService.class, PackageItemRepository.class, PackageClassController.class, PackageItemPackagePackagePropertyController.class, PackageItemPackageClassController.class, PackagePropertyController.class, UserController.class, UserRepository.class}
		, properties = {
		"server.port=8080",
		"management.server.port=9042"
})

@ActiveProfiles("localH2")
@TestPropertySource(locations = {"/application-local.properties"})
//, "/application-local-security.properties"
@Slf4j
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//@TestExecutionListeners(
//		listeners = DirtyContextBeforeAndAfterClassTestExecutionListener.class,
//		mergeMode = MergeMode.MERGE_WITH_DEFAULTS
//)
@Getter
@Setter
@ExtendWith({SpringExtension.class})
public abstract class BaseIntegrationTest {
	private final String BASE_PATH = "http://localhost:";
	@Autowired
	public HypermediaRestTemplateConfigurer configurer;
	@SpyBean
	UserServiceBean userService;
	@InjectMocks
	UserController userController;
	@InjectMocks
	UserPropertyController userUserPropertyController;
	@InjectMocks
	LogFilter logFilter;
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	CompanyMapper companyMapper;
	@Autowired
	RestTemplateGenerator restTemplateGenerator;
	@LocalServerPort
	private Integer randomServerPort;
	private KeycloakServiceManager keycloakServiceManager;
	private UsersResource usersResource;
	private UserRepresentation defaultUserRepresentation;
	private Company defaultCompany;
	private String testpasswordDefault;
	@Autowired
	private KeycloakRolesManagement keycloakRolesManagement;
	
	public BaseIntegrationTest(@Autowired KeycloakServiceManager keycloakServiceManager) {
		
		super();
		this.keycloakServiceManager = keycloakServiceManager;
		this.usersResource = this.keycloakServiceManager.getUsersResource();
		//this.userService =  Mockito.spy(new UserServiceBean(keycloakServiceManager));
		
	}
	
	@Before
	public void buildSpy() {
		
		MockitoAnnotations.openMocks(this);
		
		this.userService = Mockito.spy(new UserServiceBean(keycloakServiceManager));
	}
	
	public void verifyJsonAbstractParameter(JsonNode root, UUID id, OffsetDateTime createdDate, OffsetDateTime modifyDate, boolean deleted) {
		Assertions.assertEquals(id, UUID.fromString(root.get("id").asText()));
		Assertions.assertNotNull(createdDate);
		Assertions.assertNotNull(OffsetDateTime.parse(root.get("createDate").asText()));
		Assertions.assertNotNull(modifyDate);
		Assertions.assertNotNull(OffsetDateTime.parse(root.get("modifyDate").asText()));
		Assertions.assertEquals(deleted, Boolean.getBoolean(root.get("deleted").asText()));
	}
	
	@AfterEach
	public void afterEvery() {
		// Delete updated user
		if (this.defaultUserRepresentation != null) {
			if (!this.usersResource.search(this.defaultUserRepresentation.getUsername()).isEmpty()) {
				this.usersResource.delete(this.usersResource.search(this.defaultUserRepresentation.getUsername()).get(0).getId());
			}
		}
	}
	
	public void createGlobalCompanyProperties() {
		Random rand = new Random();
		String testpassword = UUID.randomUUID().toString();
		List<String> realmRoles = new ArrayList<>(Arrays.stream(TransitAuthorities.ADMIN_GLOBAL.getStringValues()).toList());
		
		var userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(true);
		userRepresentation.setFirstName("Dum");
		userRepresentation.setLastName("Dummy");
		userRepresentation.setEmail("dum.dum.dummy" + rand.nextInt(Integer.MAX_VALUE - 1) + "@transit-project.de");
		userRepresentation.setUsername(userRepresentation.getEmail());
		
		if (!this.usersResource.search(userRepresentation.getUsername()).isEmpty()) {
			final Response delete = this.usersResource.delete(this.usersResource.search(userRepresentation.getUsername()).get(0).getId());
		}
		var response = this.usersResource.create(userRepresentation);
		
		String userId = CreatedResponseUtil.getCreatedId(response);
		userRepresentation = this.usersResource.get(userId).toRepresentation();
		//userRepresentation = this.usersResource.search(userRepresentation.getUsername()).get(0);
		
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(testpassword);
		credential.setTemporary(false);
		this.usersResource.get(userRepresentation.getId()).resetPassword(credential);
		
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Webseite");
		compProp1.setType("text");
		var compProp2 = new GlobalCompanyPropertiesDTO();
		compProp2.setName("Email");
		compProp2.setType("text");
		var compProp3 = new GlobalCompanyPropertiesDTO();
		compProp3.setName("Beschreibung");
		compProp3.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response1 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		response1 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp2, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		response1 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp3, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		
		
	}
	
	public Integer getRandomServerPort() {
		return randomServerPort;
	}
	
	public String getBASE_PATH() {
		return BASE_PATH;
	}
	
	public void updateRestTemplate(boolean notMockEmail) {
		Random rand = new Random();
		MockitoAnnotations.openMocks(this);
		//var uS = Mockito.spy(new UserServiceBean(keycloakServiceManager));
		if (!notMockEmail) {
			log.info("mock email");
			when(this.userService.executeActionMails(any(), any())).thenReturn(true);
		}
		//Create user with own company for tests over registration Endpoint
		log.info("Start update Rest template for testing");
		String testpassword = UUID.randomUUID().toString();
		this.testpasswordDefault = testpassword;
		List<String> realmRoles = new ArrayList<>(Arrays.stream(TransitAuthorities.ADMIN_GLOBAL.getStringValues()).toList());
		realmRoles.remove("adminGlobal");
		
		var geoFactory = new GeometryFactory();
		
		
		var userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(true);
		userRepresentation.setFirstName("Dum");
		userRepresentation.setLastName("Dummy");
		userRepresentation.setEmail("dum.dum.dummy" + rand.nextInt(Integer.MAX_VALUE - 1) + "@transit-project.de");
		userRepresentation.setUsername(userRepresentation.getEmail());
		
		if (!this.usersResource.search(userRepresentation.getUsername()).isEmpty()) {
			final Response delete = this.usersResource.delete(this.usersResource.search(userRepresentation.getUsername()).get(0).getId());
		}
		var response = this.usersResource.create(userRepresentation);
		String userId = CreatedResponseUtil.getCreatedId(response);
		userRepresentation = this.usersResource.get(userId).toRepresentation();
		//userRepresentation = this.usersResource.search(userRepresentation.getUsername()).get(0);
		
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(testpassword);
		credential.setTemporary(false);
		this.usersResource.get(userRepresentation.getId()).resetPassword(credential);
		
		
		var userProperties1 = new UserPropertyDTO();
		userProperties1.setKey("Alter");
		userProperties1.setValue("500");
		userProperties1.setType("int");
		
		var userProperties2 = new UserPropertyDTO();
		userProperties2.setKey("Geschlecht");
		userProperties2.setValue("weibllich");
		userProperties2.setType("string");
		SortedSet<UserPropertyDTO> userProperties = new TreeSet<UserPropertyDTO>();
		userProperties.add(userProperties1);
		userProperties.add(userProperties2);
		var user = new UserDTORegistration();
		user.setJobPosition("Programming");
		user.setKeycloakId(UUID.fromString(userRepresentation.getId()));
		
		user.setRealmRoles(realmRoles);
		user.setUserProperties(userProperties);
		
		
		var companyProperty = new CompanyPropertyDTO();
		companyProperty.setKey("Webseite");
		companyProperty.setValue("test.com");
		companyProperty.setType("url");
		var companyPropety2 = new CompanyPropertyDTO();
		companyPropety2.setKey("Email");
		companyPropety2.setValue("dummy@test-project.de");
		companyPropety2.setType("text");
		
		var companyPropety3 = new CompanyPropertyDTO();
		companyPropety3.setKey("Beschreibung");
		companyPropety3.setValue("Liefert Waren");
		companyPropety3.setType("string");
		SortedSet<CompanyPropertyDTO> companyProperties = new TreeSet<>();
		
		SortedSet<CompanyAddressDTORegistration> companyAddresses = new TreeSet<>();
		var companyAddress = new CompanyAddressDTORegistration();
		companyAddress.setCity("TestStadt");
		companyAddress.setState("TestState");
		companyAddress.setCountry("TestLand");
		companyAddress.setStreet("Teststrasse");
		companyAddress.setIsoCode("DE");
		companyAddress.setZip("11111");
		companyAddress.setAddressExtra("50. Etage");
		companyAddress.setAddressType("Hauptadresse");
		companyAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50.346512346346, 10.3245235234532)));
		companyAddresses.add(companyAddress);
		
		var company = new CompanyDTORegistration();
		company.setName("testcompany");
		company.setProperties(companyProperties);
		company.setCompanyAddresses(companyAddresses);
		
		user.setCompany(company);
		user.setEnabled(true);
		Assertions.assertNotNull(userController);
		
		ResponseEntity<UserDTO> response1 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/users/registration", user, UserDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		realmRoles.add("adminGlobal");
		userRepresentation.setRealmRoles(realmRoles);
		keycloakRolesManagement.updateOnlyRoles(userRepresentation);
		
		var compProp1 = new GlobalCompanyPropertiesDTO();
		compProp1.setName("Webseite");
		compProp1.setType("text");
		var compProp2 = new GlobalCompanyPropertiesDTO();
		compProp2.setName("Email");
		compProp2.setType("text");
		var compProp3 = new GlobalCompanyPropertiesDTO();
		compProp3.setName("Beschreibung");
		compProp3.setType("text");
		
		ResponseEntity<GlobalCompanyPropertiesDTO> response10 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp1, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		response10 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp2, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		response10 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/globalcompanyproperties", compProp3, GlobalCompanyPropertiesDTO.class);
		Assertions.assertEquals(HttpStatus.CREATED, response10.getStatusCode());
		
		
		//UpdateCompany
		ResponseEntity<CompanyPropertyDTO> response11 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/companies/" + response1.getBody().getCompanyId() + "/companyproperties", companyProperty, CompanyPropertyDTO.class);
		response11 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/companies/" + response1.getBody().getCompanyId() + "/companyproperties", companyPropety2, CompanyPropertyDTO.class);
		response11 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).postForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/companies/" + response1.getBody().getCompanyId() + "/companyproperties", companyPropety3, CompanyPropertyDTO.class);
		
		
		response1 = getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())).getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/users/" + response1.getBody().getId(), UserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
		var x = new KeycloakRolesManagement(this.keycloakServiceManager);
		
		this.usersResource.get(userRepresentation.getId()).roles().realmLevel().add(Collections.singletonList(this.keycloakServiceManager.getRealmResource().roles().get(TransitAuthorities.ADMIN_GLOBAL.getStringValue()).toRepresentation()));
		this.defaultUserRepresentation = x.getUserRepresentation(Objects.requireNonNull(response1.getBody()).getKeycloakId());
		Assertions.assertTrue(this.defaultUserRepresentation.getRealmRoles().contains(TransitAuthorities.ADMIN_GLOBAL.getStringValue()));
		this.restTemplateGenerator.setTestRestTemplate(getRestTemplateGenerator().getTestRestTemplate(userRepresentation.getUsername(), testpassword, String.valueOf(getRandomServerPort())));
		this.defaultCompany = companyMapper.toEntity(this.restTemplateGenerator.getTestRestTemplate().getRestTemplate().getForEntity(getBASE_PATH() + getRandomServerPort() + "/api/v1 " + "/companies/" + response1.getBody().getCompanyId().toString(), CompanyDTO.class).getBody());
		
		log.info("Ready update Rest template for testing");
		
	}
	
	
}

