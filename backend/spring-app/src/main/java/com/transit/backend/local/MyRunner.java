package com.transit.backend.local;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.service.helper.KeycloakRolesManagement;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.authmodel.TransitAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Profile(value = {"localH2"})
@Slf4j
public class MyRunner implements CommandLineRunner {
	
	
	private static final Logger logger = LoggerFactory.getLogger(MyRunner.class);
	private final UsersResource userResource;
	private final Environment env;
	@Autowired
	DataSource dataSource;
	@Autowired
	private PackagePropertyRepository packagePropertiesReporitory;
	@Autowired
	private PackageClassRepository packageClassReporitory;
	@Autowired
	private PackageItemPackagePropertyRepository packageItemPackagePropertyRepository;
	@Autowired
	private PackageItemRepository packageReporitory;
	@Autowired
	private DeliveryMethodRepository deliveryMethodRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderPropertyRepository orderPropertyRepository;
	
	@Autowired
	private CarRepository carRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private WarehouseRepository warehouseRepository;
	
	
	@Autowired
	private CompanyDeliveryAreaRepository companyDeliveryAreaRepository;
	
	
	@Autowired
	private RightsManageService generateEntryRight;
	
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JwtDecoder jwtDecoder;
	
	@Autowired
	private KeycloakRolesManagement keycloakRolesManagement;
	
	
	private int user1Int;
	
	private int user2Int;
	
	
	public MyRunner(Environment env) {
		this.restTemplate = new RestTemplate();
		this.env = env;
		Keycloak kc = KeycloakBuilder.builder()
				.serverUrl(env.getProperty("keycloak.admin.client.url"))
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm(env.getProperty("keycloak.admin.client.realm"))
				.clientId(env.getProperty("keycloak.admin.client.id"))
				.clientSecret(env.getProperty("keycloak.admin.client.secret"))
				.build();
		this.userResource = kc.realm(env.getProperty("keycloak.admin.client.realm")).users();
		
	}
	
	@Override
	public void run(String... args) throws Exception {
		log.error("TestDB");
		
		boolean createTestData = false;

//		var x = env.getProperty("createTestData");
//		if (x != null) {
//			createTestData = true;
//		}
		
		if (createTestData) {
			Random rand = new Random();
			user1Int = rand.nextInt(Integer.MAX_VALUE - 1);
			String email = "dummymy.dum" + user1Int + "@transit-project.de";
			Map<UUID, Class<?>> entriesRights = new HashMap<>();
			Map<UUID, Class<?>> entriesRights2 = new HashMap<>();
			clearDatabase();
			var user1 = new User();
			user1.setId(UUID.randomUUID());
			user1.setJobPosition("Programming");
			user1.setCreatedBy(email);
			user1.setLastModifiedBy(email);
			user1 = userRepository.saveAndFlush(user1);
			user1.setCreatedBy(email);
			user1.setLastModifiedBy(email);
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			
			
			var company1 = new Company();
			company1.setName("Logistics Living Lab - Universität Leipzig");
			company1.setCreatedBy(email);
			company1.setLastModifiedBy(email);
			
			company1.addCompanyProperty(new CompanyProperty("Website", "https://OA.org/", "text", company1));
			for (var prop : company1.getCompanyProperties()) {
				prop.setCreatedBy(email);
				prop.setLastModifiedBy(email);
			}
			company1 = companyRepository.save(company1);
			
			
			entriesRights.put(user1.getId(), User.class);
			entriesRights.put(company1.getId(), Company.class);
			
			var deliveryArea = new CompanyDeliveryArea();
			deliveryArea.setCompany(company1);
			deliveryArea.setDeliveryAreaZips(new HashSet<>(Arrays.asList(new String[]{"00000", "00001"})));
			deliveryArea.setCreatedBy(email);
			deliveryArea.setLastModifiedBy(email);
			
			deliveryArea = companyDeliveryAreaRepository.save(deliveryArea);
			//Für den test ausgeschalten
			entriesRights.put(deliveryArea.getId(), CompanyDeliveryArea.class);
			
			var geoFactory = new GeometryFactory();
			
			
			var address1 = new Address();
			address1.setLocationPoint(geoFactory.createPoint(new Coordinate(50.23, 10.23)));
			address1.setCity("Leipzig");
			address1.setState("Sachsen");
			address1.setCountry("Deutschland");
			address1.setStreet("Grimmaische Straße 2-4");
			address1.setIsoCode("DE");
			address1.setZip("04109");
			address1.setAddressExtra("2. Etage");
			
			address1.setCreatedBy(email);
			address1.setLastModifiedBy(email);
			
			address1 = addressRepository.save(address1);
			
			
			var companyAddress1 = new CompanyAddress();
			companyAddress1.setId(new CompanyAddressId(company1.getId(), address1.getId()));
			companyAddress1.setAddress(address1);
			companyAddress1.setCompany(company1);
			companyAddress1.setAddressType("Hauptanschrift");
			
			companyAddress1.setCreatedBy(email);
			companyAddress1.setLastModifiedBy(email);
			
			companyAddressRepository.save(companyAddress1);
			
			
			//userUpdate
			user1.setJobPosition("Programming");
			user1.setCompany(company1);
			user1.addUserProperties(new UserProperty("Property1", "PropertyValue1", "text", user1));
			for (var prop : user1.getUserProperties()) {
				prop.setCreatedBy(email);
				prop.setLastModifiedBy(email);
			}
			
			var userRepresentation1 = new UserRepresentation();
			userRepresentation1.setEnabled(true);
			userRepresentation1.setFirstName("Dum");
			userRepresentation1.setLastName("Dummymy");
			
			userRepresentation1.setEmail("dummymy.dum" + user1Int + "@transit-project.de");
			userRepresentation1.setUsername(userRepresentation1.getEmail());
			userRepresentation1.setRealmRoles(List.of(TransitAuthorities.ADMIN_GLOBAL.getStringValues()));
			if (!this.userResource.search(userRepresentation1.getUsername()).isEmpty()) {
				this.userResource.delete(this.userResource.search(userRepresentation1.getUsername()).get(0).getId());
			}
			
			this.userResource.create(userRepresentation1);
			userRepresentation1 = this.userResource.search(userRepresentation1.getUsername()).get(0);
			
			CredentialRepresentation credential = new CredentialRepresentation();
			credential.setType(CredentialRepresentation.PASSWORD);
			credential.setValue(env.getProperty("keycloak.local.user.password"));
			credential.setTemporary(false);
			this.userResource.get(userRepresentation1.getId()).resetPassword(credential);
			
			var userRepresentation = this.userResource.search(userRepresentation1.getUsername()).get(0);
			
			user1.setKeycloakId(UUID.fromString(userRepresentation.getId()));
			user1.setKeycloakEmail(userRepresentation.getEmail());
			user1 = userRepository.saveAndFlush(user1);
			
			//starting
			entriesRights.put(user1.getId(), User.class);
			entriesRights.put(company1.getId(), Company.class);
			entriesRights.put(address1.getId(), Address.class);
			
			
			var packageItem1 = new PackageItem();
			packageItem1.setCreatedBy(email);
			packageItem1.setLastModifiedBy(email);
			
			var packageItem2 = new PackageItem();
			packageItem2.setCreatedBy(email);
			packageItem2.setLastModifiedBy(email);
			packageItem1.setHeightCm(12);
			packageItem2.setHeightCm(14);
			
			SortedSet<PackagePackageProperty> packageItemProperties = new TreeSet<>();
			
			
			var packageProperties = new PackageProperty();
			var packageItemPackageProperties = new PackagePackageProperty();
			packageProperties.setCreatedBy(email);
			packageProperties.setLastModifiedBy(email);
			packageItemPackageProperties.setCreatedBy(email);
			packageItemPackageProperties.setLastModifiedBy(email);
			packageProperties.setKey("Zerbrechlich");
			packageProperties.setDefaultValue("true");
			packageProperties.setType("boolean");
			packageProperties.setCompany(company1);
			packageItemPackageProperties.setKey(packageProperties.getKey());
			packageItemPackageProperties.setValue(packageProperties.getDefaultValue());
			packageItemPackageProperties.setType(packageProperties.getType());
			packageItemPackageProperties.setPackageItem(packageItem1);
			packageItemProperties.add(packageItemPackageProperties);
			packageProperties = packagePropertiesReporitory.save(packageProperties);
			entriesRights.put(packageProperties.getId(), PackageProperty.class);
			
			packageProperties = new PackageProperty();
			packageItemPackageProperties = new PackagePackageProperty();
			packageProperties.setCreatedBy(email);
			packageProperties.setLastModifiedBy(email);
			packageItemPackageProperties.setCreatedBy(email);
			packageItemPackageProperties.setLastModifiedBy(email);
			packageProperties.setKey("Druckfestigkeit");
			packageProperties.setDefaultValue("800");
			packageProperties.setType("int");
			packageProperties.setCompany(company1);
			packageItemPackageProperties.setType(packageProperties.getType());
			packageItemPackageProperties.setKey(packageProperties.getKey());
			packageItemPackageProperties.setValue(packageProperties.getDefaultValue());
			packageItemPackageProperties.setPackageItem(packageItem1);
			packageItemProperties.add(packageItemPackageProperties);
			
			packageProperties = packagePropertiesReporitory.save(packageProperties);
			entriesRights.put(packageProperties.getId(), PackageProperty.class);
			
			
			packageProperties = new PackageProperty();
			packageItemPackageProperties = new PackagePackageProperty();
			packageProperties.setCreatedBy(email);
			packageProperties.setLastModifiedBy(email);
			packageItemPackageProperties.setCreatedBy(email);
			packageItemPackageProperties.setLastModifiedBy(email);
			packageProperties.setKey("Dichte");
			packageProperties.setDefaultValue(" 2500 kg/m3");
			packageProperties.setType("string");
			packageProperties.setCompany(company1);
			packageItemPackageProperties.setType(packageProperties.getType());
			packageItemPackageProperties.setKey(packageProperties.getKey());
			packageItemPackageProperties.setValue(packageProperties.getDefaultValue());
			packageItemPackageProperties.setPackageItem(packageItem1);
			packageItemProperties.add(packageItemPackageProperties);
			packageProperties = packagePropertiesReporitory.save(packageProperties);
			entriesRights.put(packageProperties.getId(), PackageProperty.class);
			
			packageItem1.setPackagePackageProperties(packageItemProperties);
			
			packageItemProperties = new TreeSet<>();
			packageProperties = new PackageProperty();
			packageItemPackageProperties = new PackagePackageProperty();
			packageProperties.setCreatedBy(email);
			packageProperties.setLastModifiedBy(email);
			packageItemPackageProperties.setCreatedBy(email);
			packageItemPackageProperties.setLastModifiedBy(email);
			packageProperties.setKey("Holzfeuchte");
			packageProperties.setDefaultValue("20%");
			packageProperties.setType("string");
			packageProperties.setCompany(company1);
			packageItemPackageProperties.setType(packageProperties.getType());
			packageItemPackageProperties.setKey(packageProperties.getKey());
			packageItemPackageProperties.setValue(packageProperties.getDefaultValue());
			packageItemPackageProperties.setPackageItem(packageItem2);
			packageItemProperties.add(packageItemPackageProperties);
			packageProperties = packagePropertiesReporitory.save(packageProperties);
			entriesRights.put(packageProperties.getId(), PackageProperty.class);
			
			packageItem2.setPackagePackageProperties(packageItemProperties);
			
			
			var packageClass = new PackageClass();
			packageClass.setCreatedBy(email);
			packageClass.setLastModifiedBy(email);
			packageClass.setName("Palette");
			packageClass = packageClassReporitory.save(packageClass);
			
			entriesRights.put(packageClass.getId(), PackageClass.class);
			
			packageItem1.setPackageClass(packageClass);
			
			packageClass = new PackageClass();
			packageClass.setCreatedBy(email);
			packageClass.setLastModifiedBy(email);
			packageClass.setName("Pappkarton");
			packageClass = packageClassReporitory.save(packageClass);
			
			entriesRights.put(packageClass.getId(), PackageClass.class);
			
			packageItem2.setPackageClass(packageClass);
			
			
			//packageItem2.setDeliveryMethod("method2");
			
			var deliveryMethod = new DeliveryMethod();
			deliveryMethod.setCreatedBy(email);
			deliveryMethod.setLastModifiedBy(email);
			deliveryMethod.setDeliveryMethodName("Express");
			deliveryMethod = deliveryMethodRepository.save(deliveryMethod);
			
			
			entriesRights.put(deliveryMethod.getId(), DeliveryMethod.class);
			deliveryMethod = new DeliveryMethod();
			deliveryMethod.setCreatedBy(email);
			deliveryMethod.setLastModifiedBy(email);
			deliveryMethod.setDeliveryMethodName("Schwertransport");
			deliveryMethod = deliveryMethodRepository.save(deliveryMethod);
			
			entriesRights.put(deliveryMethod.getId(), DeliveryMethod.class);
			;
			packageItem1.setExplosive(false);
			packageItem1.setFrost(false);
			packageItem2.setExplosive(false);
			packageItem2.setFrost(false);
			packageItem1 = packageReporitory.saveAndFlush(packageItem1);
			packageItem2 = packageReporitory.saveAndFlush(packageItem2);
			
			entriesRights.put(packageItem1.getId(), PackageItem.class);
			packageItem1.getProperties().forEach(property -> entriesRights.put(property.getId(), PackagePackageProperty.class));
			entriesRights.put(packageItem2.getId(), PackageItem.class);
			packageItem2.getProperties().forEach(property -> entriesRights.put(property.getId(), PackagePackageProperty.class));
			//session.evict(packageItem1);
			//session.evict(packageItem2);
			
			
			var address2 = new Address();
			address2.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435345345, 10.43563457635768)));
			address2.setCity("Berlin");
			address2.setState("Berlin");
			address2.setCountry("Deutschland");
			address2.setStreet("Pariser Platz");
			address2.setIsoCode("DE");
			address2.setZip("10117");
			address2.setAddressExtra("0. Etage");
			
			address2.setCreatedBy(email);
			address2.setLastModifiedBy(email);
			
			address2 = addressRepository.save(address2);
			entriesRights.put(address2.getId(), Address.class);
			
			var address3 = new Address();
			address3.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435345345, 10.43563457635768)));
			address3.setCity("Berlin");
			address3.setState("Berlin");
			address3.setCountry("Deutschland");
			address3.setStreet("PariserPlatz");
			address3.setIsoCode("DE");
			address3.setZip("11131");
			address3.setAddressExtra("10. Etage");
			
			address3.setCreatedBy(email);
			address3.setLastModifiedBy(email);
			
			addressRepository.save(address3);
			entriesRights.put(address3.getId(), Address.class);
			
			Set<PackageItem> packageItems = new HashSet<>();
			
			
			packageItems.add(packageItem1);
			packageItems.add(packageItem2);
			var order = new Order();
			order.setOrderStatus(OrderStatus.OPEN);
			order.setAddressFrom(addressRepository.saveAndFlush(address1.clone()));
			order.setAddressTo(addressRepository.saveAndFlush(address2.clone()));
			order.setAddressBilling(addressRepository.saveAndFlush(address3.clone()));
			order.setPackageItems(packageItems);
			order.setCreatedBy(email);
			order.setLastModifiedBy(email);
			SortedSet<OrderProperty> orderProperties = new TreeSet<>();
			var orderProperty = new OrderProperty();
			orderProperty.setKey("T>ime WIndow Delivery");
			orderProperty.setValue("10-11 Uhr");
			orderProperty.setType("text");
			orderProperty.setOrder(order);
			orderProperty.setCreatedBy(email);
			orderProperty.setLastModifiedBy(email);
			orderProperties.add(orderProperty);
			
			order.setOrderProperties(orderProperties);
			
			order.setCompany(company1);
			
			
			order = orderRepository.save(order);
			
			entriesRights.put(order.getId(), Order.class);
			entriesRights.put(order.getAddressFrom().getId(), Address.class);
			entriesRights.put(order.getAddressTo().getId(), Address.class);
			entriesRights.put(order.getAddressBilling().getId(), Address.class);
			order.getProperties().forEach(property -> entriesRights.put(property.getId(), OrderProperty.class));
			
			var order2 = new Order();
			order2.setOrderStatus(OrderStatus.OPEN);
			order2.setAddressFrom(addressRepository.saveAndFlush(address1.clone()));
			order2.setAddressTo(addressRepository.saveAndFlush(address2.clone()));
			order2.setAddressBilling(addressRepository.saveAndFlush(address3.clone()));
			order2.setPackageItems(packageItems);
			order2.setCreatedBy(email);
			order2.setLastModifiedBy(email);
			SortedSet<OrderProperty> orderProperties2 = new TreeSet<>();
			var orderProperty2 = new OrderProperty();
			orderProperty2.setKey("Time Window Delivery");
			orderProperty2.setValue("12-13 Uhr");
			orderProperty2.setType("text");
			orderProperty2.setOrder(order2);
			orderProperty2.setCreatedBy(email);
			orderProperty2.setLastModifiedBy(email);
			orderProperties2.add(orderProperty2);
			
			order2.setOrderProperties(orderProperties2);
			order2.setParentOrder(order);
			order2.setSuborderType(true);
			
			
			order2.setCompany(company1);
			
			orderRepository.save(order2);
			
			entriesRights.put(order2.getId(), Order.class);
			entriesRights.put(order2.getAddressFrom().getId(), Address.class);
			entriesRights.put(order2.getAddressTo().getId(), Address.class);
			entriesRights.put(order2.getAddressBilling().getId(), Address.class);
			order2.getProperties().forEach(property -> entriesRights.put(property.getId(), OrderProperty.class));
			
			
			var car = new Car();
			car.setPlate("dhgbegi");
			car.setCapacity("3254328g");
			car.setCreatedBy(email);
			car.setLastModifiedBy(email);
			var carProperty = new CarProperty();
			carProperty.setCar(car);
			carProperty.setType("text");
			carProperty.setValue("test");
			carProperty.setKey("testkey");
			carProperty.setCreatedBy(email);
			carProperty.setLastModifiedBy(email);
			
			car.addCarProperty(carProperty);
			SortedSet<Location> carLocations = new TreeSet<>();
			var location = new Location();
			location.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435, 10.768)));
			location.setCreatedBy(email);
			location.setLastModifiedBy(email);
			location = locationRepository.save(location);
			entriesRights.put(location.getId(), Location.class);
			carLocations.add(location);
			var location2 = new Location();
			location2.setLocationPoint(geoFactory.createPoint(new Coordinate(55.435, 10.768)));
			location2.setCreatedBy(email);
			location2.setLastModifiedBy(email);
			location2 = locationRepository.save(location2);
			entriesRights.put(location2.getId(), Location.class);
			carLocations.add(location2);
			car.setLocations(carLocations);
			carRepository.save(car);
			entriesRights.put(car.getId(), Car.class);
			car.getProperties().forEach(property -> entriesRights.put(property.getId(), CarProperty.class));
			
			var warehouse = new Warehouse();
			warehouse.setName("TestWarehouse");
			warehouse.setCapacity(4364364l);
			warehouse.setCreatedBy(email);
			warehouse.setLastModifiedBy(email);
			
			var warehouseProperty = new WarehouseProperty();
			warehouseProperty.setWarehouse(warehouse);
			warehouseProperty.setType("text");
			warehouseProperty.setValue("test");
			warehouseProperty.setKey("testkey");
			warehouseProperty.setCreatedBy(email);
			warehouseProperty.setLastModifiedBy(email);
			
			warehouse.addWarehouseProperty(warehouseProperty);
			var warehouseAddress = new Address();
			warehouseAddress.setLocationPoint(geoFactory.createPoint(new Coordinate(50.435, 10.768)));
			warehouseAddress.setCity("TestStadt");
			warehouseAddress.setState("TestState");
			warehouseAddress.setCountry("TestLand");
			warehouseAddress.setStreet("Teststrasse");
			warehouseAddress.setIsoCode("DE");
			warehouseAddress.setZip("11111");
			warehouseAddress.setAddressExtra("120. Etage");
			warehouseAddress.setCreatedBy(email);
			warehouseAddress.setLastModifiedBy(email);
			warehouseAddress = addressRepository.save(warehouseAddress);
			entriesRights.put(warehouseAddress.getId(), Address.class);
			warehouse.setAddress(warehouseAddress);
			warehouse = warehouseRepository.save(warehouse);
			entriesRights.put(warehouse.getId(), Warehouse.class);
			warehouse.getProperties().forEach(property -> entriesRights.put(property.getId(), WarehouseProperty.class));
			for (var usKey : this.userResource.list(0, this.userResource.count())) {
				if (usKey.getEmail().endsWith("@infai.org")) {
					var user = new User();
					user.setId(UUID.randomUUID());
					user.setJobPosition("Programming");
					user.setCreatedBy(usKey.getEmail());
					user.setLastModifiedBy(usKey.getEmail());
					user.setKeycloakId(UUID.fromString(usKey.getId()));
					user.setKeycloakEmail(usKey.getEmail());
					user = userRepository.saveAndFlush(user);
					user.setCreatedBy(usKey.getEmail());
					user.setLastModifiedBy(usKey.getEmail());
					user.setCompany(company1);
					user = userRepository.saveAndFlush(user);
					entriesRights.put(user.getId(), User.class);
				}
			}
			
			var userId2 = secondCompany(entriesRights2);
			
			session.getTransaction().commit();
			session.close();
			generateEntryRight.registerCompany(company1.getId());
			
			login();
			TimeUnit.SECONDS.sleep(10);
			for (var entry : entriesRights.entrySet()) {
				generateEntryRight.createEntityAndConnectIt(entry.getKey(), entry.getValue().getSimpleName(), entry.getValue(), user1.getId());
			}
			login2();
			TimeUnit.SECONDS.sleep(10);
			for (var entry : entriesRights2.entrySet()) {
				
				
				generateEntryRight.createEntityAndConnectIt(entry.getKey(), entry.getValue().getSimpleName(), entry.getValue(), userId2);
			}
		
/*
		packageReporitory.findAll().forEach((packageItem) -> {
			logger.info("{}", packageItem);
		});
*/
			
			
			//SELECT distinct key, value, packageItem.id, delivery_method, class_name   FROm package_properties join packageItem_properties join packageItem join packageItem_package_class join package_class order by packageItem.id, key
		}
		log.error("Started Success Test");
	}
	
	public void clearDatabase() throws SQLException {
		try (Connection c = dataSource.getConnection()) {
			Statement s = c.createStatement();
			
			
			//	 Replace "public" with your schema name if needed
//			String query = "SELECT 'TRUNCATE TABLE ' || schemaname || '.' || tablename || ' CASCADE;' " +
//					"FROM pg_tables " +
//					"WHERE schemaname IN ('public') " +
//					"ORDER BY schemaname, tablename; ";

//			String query = "CREATE OR REPLACE PROCEDURE sp_Drop_Endpoint_Tables()\n" +
//					"\t AS $$\n" +
//					"DECLARE v_cmd varchar(4000);\n" +
//					"DECLARE cmds CURSOR FOR\n" +
//					"SELECT 'truncate table [' || Table_Name || ']'\n" +
//					"FROM PUBLIC.TABLES\n" +
//					"WHERE Table_Name LIKE 'tbl_%';\n" +
//					"BEGIN\n" +
//					"OPEN cmds;\n" +
//					"WHILE 1 = 1\n" +
//					"LOOP\n" +
//					"    FETCH cmds INTO v_cmd;\n" +
//					"    IF @@fetch_status != 0 THEN EXIT;\n" +
//					"    END IF;\n" +
//					"    EXECUTE(v_cmd);\n" +
//					"END LOOP;\n" +
//					"CLOSE cmds;\n" +
//					"END;\n" +
//					"$$ LANGUAGE plpgsql;";


//			s.execute(query);
//			s.execute("sp_Drop_Endpoint_Tables()");
//			// Get a list of table names
//			ResultSet rs = s.executeQuery(
//					"SELECT tablename FROM pg_tables WHERE schemaname = public");
//
//			// Truncate each table
//			while (rs.next()) {
//				log.error(rs.getString(1));
//				s.execute("TRUNCATE TABLE " + schema + "." + rs.getString(1) + " CASCADE");
//			}
			
			// Disable FK
//			s.execute("SET REFERENTIAL_INTEGRITY FALSE");
//
//			// Find all tables and truncate them
//			Set<String> tables = new HashSet<String>();
//			ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
//			while (rs.next()) {
//				tables.add(rs.getString(1));
//			}
//			rs.close();
//			for (String table : tables) {
//				log.info("clean table " + table);
//				s.executeUpdate("TRUNCATE TABLE " + table);
//			}
////
//			// Idem for sequences
//			Set<String> sequences = new HashSet<String>();
//			rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
//			while (rs.next()) {
//				sequences.add(rs.getString(1));
//			}
//			rs.close();
//			for (String seq : sequences) {
//				s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
//			}
//
//			// Enable FK
//			s.execute("SET REFERENTIAL_INTEGRITY TRUE");
			
			
			// Befehl zum Deaktivieren der Fremdschlüsselüberprüfung
			s.execute("SET session_replication_role = replica;");
			
			// Abrufen der Tabellennamen im angegebenen Schema
			String query = "SELECT table_name FROM information_schema.tables WHERE table_schema like " + "'public';";
			try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
				ResultSet resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					
					String tableName = resultSet.getString("table_name");
					//log.error(tableName);
					List<String> notTable = Arrays.asList("geography_columns", "geometry_columns", "spatial_ref_sys");
					// Leeren der Tabelle
					if (!notTable.contains(tableName)) {
						s.executeUpdate("TRUNCATE TABLE " + "public" + "." + tableName + " CASCADE");
					}
				}
			}
			
			// Befehl zum Aktivieren der Fremdschlüsselüberprüfung
			s.execute("SET session_replication_role = origin;");
			
			
			s.close();
			log.info("End CLean Tables");
		}
	}
	
	public UUID secondCompany(Map<UUID, Class<?>> entriesRights) {
		Random rand = new Random();
		user2Int = rand.nextInt(Integer.MAX_VALUE - 1);
		String email2 = "dummymy2.dum" + user2Int + "@transit-project.de";
		
		var user2 = new User();
		user2.setId(UUID.randomUUID());
		user2.setJobPosition("Serve Packages");
		user2.setCreatedBy(email2);
		user2.setLastModifiedBy(email2);
		user2 = userRepository.saveAndFlush(user2);
		user2.setCreatedBy(email2);
		user2.setLastModifiedBy(email2);
		
		
		var company2 = new Company();
		company2.setName("Firma Berlin XY");
		company2.setCreatedBy(email2);
		company2.setLastModifiedBy(email2);
		
		company2.addCompanyProperty(new CompanyProperty("Website", "https://test2.org/", "text", company2));
		for (var prop : company2.getCompanyProperties()) {
			prop.setCreatedBy(email2);
			prop.setLastModifiedBy(email2);
		}
		company2 = companyRepository.save(company2);
		
		
		entriesRights.put(user2.getId(), User.class);
		entriesRights.put(company2.getId(), Company.class);
		
		var deliveryArea = new CompanyDeliveryArea();
		deliveryArea.setCompany(company2);
		deliveryArea.setDeliveryAreaZips(new HashSet<>(Arrays.asList(new String[]{"00000", "00001"})));
		deliveryArea.setCreatedBy(email2);
		deliveryArea.setLastModifiedBy(email2);
		
		deliveryArea = companyDeliveryAreaRepository.save(deliveryArea);
		entriesRights.put(deliveryArea.getId(), CompanyDeliveryArea.class);
		
		var geoFactory = new GeometryFactory();
		
		
		var address2 = new Address();
		address2.setLocationPoint(geoFactory.createPoint(new Coordinate(30.30, 20.12)));
		address2.setCity("TestStadt123");
		address2.setState("TestState123");
		address2.setCountry("TestLand123");
		address2.setStreet("Teststrasse123");
		address2.setIsoCode("DE");
		address2.setZip("11123");
		address2.setAddressExtra("10. Etage");
		
		address2.setCreatedBy(email2);
		address2.setLastModifiedBy(email2);
		
		address2 = addressRepository.save(address2);
		
		var companyAddress1 = new CompanyAddress();
		companyAddress1.setId(new CompanyAddressId(company2.getId(), address2.getId()));
		companyAddress1.setAddress(address2);
		companyAddress1.setCompany(company2);
		companyAddress1.setAddressType("Hauptanschrift");
		
		companyAddress1.setCreatedBy(email2);
		companyAddress1.setLastModifiedBy(email2);
		
		companyAddressRepository.save(companyAddress1);
		
		
		//userUpdate
		user2.setJobPosition("Deliver Packages");
		user2.setCompany(company2);
		user2.addUserProperties(new UserProperty("Property2", "PropertyValue2", "text", user2));
		for (var prop : user2.getUserProperties()) {
			prop.setCreatedBy(email2);
			prop.setLastModifiedBy(email2);
		}
		
		var userRepresentation2 = new UserRepresentation();
		userRepresentation2.setEnabled(true);
		userRepresentation2.setFirstName("Dum2");
		userRepresentation2.setLastName("Dummymy2");
		
		userRepresentation2.setEmail("dummymy2.dum" + user2Int + "@transit-project.de");
		userRepresentation2.setUsername(userRepresentation2.getEmail());
		
		if (!this.userResource.search(userRepresentation2.getUsername()).isEmpty()) {
			this.userResource.delete(this.userResource.search(userRepresentation2.getUsername()).get(0).getId());
		}
		
		this.userResource.create(userRepresentation2);
		userRepresentation2 = this.userResource.search(userRepresentation2.getUsername()).get(0);
		userRepresentation2.setRealmRoles(List.of(TransitAuthorities.ADMIN_GLOBAL.getStringValues()));
		keycloakRolesManagement.updateOnlyRoles(userRepresentation2);
		
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(env.getProperty("keycloak.local.user.password123"));
		credential.setTemporary(false);
		this.userResource.get(userRepresentation2.getId()).resetPassword(credential);
		
		var userRepresentation = this.userResource.search(userRepresentation2.getUsername()).get(0);
		
		user2.setKeycloakId(UUID.fromString(userRepresentation.getId()));
		user2.setKeycloakEmail(userRepresentation.getEmail());
		user2 = userRepository.saveAndFlush(user2);
		
		//starting
		entriesRights.put(user2.getId(), User.class);
		entriesRights.put(company2.getId(), Company.class);
		entriesRights.put(address2.getId(), Address.class);
		
		for (var usKey : this.userResource.list(0, this.userResource.count())) {
			if (usKey.getEmail().endsWith("@wifa.uni-leipzig.de")) {
				var user = new User();
				user.setId(UUID.randomUUID());
				user.setJobPosition("Programming");
				user.setCreatedBy(usKey.getEmail());
				user.setLastModifiedBy(usKey.getEmail());
				user.setKeycloakId(UUID.fromString(usKey.getId()));
				user.setKeycloakEmail(usKey.getEmail());
				user = userRepository.saveAndFlush(user);
				user.setCreatedBy(usKey.getEmail());
				user.setLastModifiedBy(usKey.getEmail());
				user.setCompany(company2);
				user = userRepository.saveAndFlush(user);
				entriesRights.put(user.getId(), User.class);
			}
		}
		
		generateEntryRight.registerCompany(company2.getId());
		return user2.getId();
	}
	
	
	public void login() throws JsonProcessingException {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		
		map.add("username", "dummymy.dum" + user1Int + "@transit-project.de");
		
		map.add("password", env.getProperty("keycloak.local.user.password"));
		map.add("port", "8080");
		
		map.add("client_id", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-id"));
		map.add("grant_type", "password");
		map.add("client_secret", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-secret"));
		map.add("scope", "openid");
		map.add("redirect_uri", "http://localhost:8080");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		String responseStr = restTemplate.postForObject(Objects.requireNonNull(env.getProperty("spring.security.oauth2.client.provider.keycloak.token-uri")), request, String.class);
		JsonNode root = objectMapper.readTree(responseStr);
		var jwt = jwtDecoder.decode(root.get("access_token").asText());
		var token = new JwtAuthenticationToken(jwt);
		SecurityContextHolder.getContext().setAuthentication(token);
		
	}
	
	public void login2() throws JsonProcessingException {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		
		map.add("username", "dummymy2.dum" + user2Int + "@transit-project.de");
		
		map.add("password", env.getProperty("keycloak.local.user.password123"));
		map.add("port", "8080");
		
		map.add("client_id", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-id"));
		map.add("grant_type", "password");
		map.add("client_secret", env.getProperty("spring.security.oauth2.client.registration.keycloak.client-secret"));
		map.add("scope", "openid");
		map.add("redirect_uri", "http://localhost:8080");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		String responseStr = restTemplate.postForObject(Objects.requireNonNull(env.getProperty("spring.security.oauth2.client.provider.keycloak.token-uri")), request, String.class);
		JsonNode root = objectMapper.readTree(responseStr);
		var jwt = jwtDecoder.decode(root.get("access_token").asText());
		var token = new JwtAuthenticationToken(jwt);
		SecurityContextHolder.getContext().setAuthentication(token);
		
	}
}