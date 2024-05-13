package com.transit.backend.config;

import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.ObjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class UpdateRightsNewFields implements CommandLineRunner {
	
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CarPropertyRepository carPropertyRepository;
	@Autowired
	private CarRepository carRepository;
	
	
	@Autowired
	private CompanyCustomerRepository companyCustomerRepository;
	
	@Autowired
	private CompanyDeliveryAreaRepository companyDeliveryAreaRepository;
	
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private ContactPersonRepository contactPersonRepository;
	@Autowired
	private CostDefaultPropertyRepository costDefaultPropertyRepository;
	
	@Autowired
	private CostPropertyRepository costPropertyRepository;
	
	@Autowired
	private CostRepository costRepository;
	
	@Autowired
	private DeliveryMethodRepository deliveryMethodRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private OrderPropertyRepository orderPropertyRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderRouteRepository orderRouteRepository;
	@Autowired
	private OrderTypePropertyRepository orderTypePropertyRepository;
	@Autowired
	private OrderTypeRepository orderTypeRepository;
	@Autowired
	private PackageClassRepository packageClassRepository;
	@Autowired
	private PackageItemPackagePropertyRepository packageItemPackagePropertyRepository;
	@Autowired
	private PackageItemRepository packageItemRepository;
	@Autowired
	private PackagePropertyRepository packagePropertyRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private UserPropertyRepository userPropertyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WarehousePropertyRepository warehousePropertyRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	@Autowired
	private DefaultSharingRightsRepository defaultSharingRightsRepository;
	@Autowired
	private ChatEntryRepository chatEntryRepository;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	
	@Autowired
	private CompanyFavoriteRepository companyFavoriteRepository;
	@Autowired
	private OrderCommentChatRepository orderCommentChatRepository;
	
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private ObjectService objectService;
	
	
	@Override
	public void run(String... args) {
		log.error("Start update Rights");
		addressRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Address.class), entry.getCreatedBy(), entry.getId());
		});
		
		carPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CarProperty.class), entry.getCreatedBy(), entry.getId());
		});
		
		carRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Car.class), entry.getCreatedBy(), entry.getId());
		});
		
		companyCustomerRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Customer.class), entry.getCreatedBy(), entry.getId());
		});
		companyDeliveryAreaRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CompanyDeliveryArea.class), entry.getCreatedBy(), entry.getId());
		});
		companyPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CompanyProperty.class), entry.getCreatedBy(), entry.getId());
		});
		
		companyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Company.class), entry.getCreatedBy(), entry.getId());
			updateRights(FieldUtils.getAllFields(Company.class), entry.getCreatedBy(), companyIdToCompanyOIDRepository.findById(entry.getId()).get().getCompanyOID());
		});
		contactPersonRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(ContactPerson.class), entry.getCreatedBy(), entry.getId());
		});
		costDefaultPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CostDefaultProperty.class), entry.getCreatedBy(), entry.getId());
		});
		costPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CostProperty.class), entry.getCreatedBy(), entry.getId());
		});
		costRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Cost.class), entry.getCreatedBy(), entry.getId());
		});
		deliveryMethodRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(DeliveryMethod.class), entry.getCreatedBy(), entry.getId());
		});
		locationRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Location.class), entry.getCreatedBy(), entry.getId());
		});
		orderPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(OrderProperty.class), entry.getCreatedBy(), entry.getId());
		});
		orderRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Order.class), entry.getCreatedBy(), entry.getId());
		});
		orderRouteRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(OrderRoute.class), entry.getCreatedBy(), entry.getId());
		});
		orderTypePropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(OrderTypeProperty.class), entry.getCreatedBy(), entry.getId());
		});
		orderTypeRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(OrderType.class), entry.getCreatedBy(), entry.getId());
		});
		packageClassRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(PackageClass.class), entry.getCreatedBy(), entry.getId());
		});
		packageItemPackagePropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(PackagePackageProperty.class), entry.getCreatedBy(), entry.getId());
		});
		packageItemPackagePropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(PackagePackageProperty.class), entry.getCreatedBy(), entry.getId());
		});
		
		packageItemRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(PackageItem.class), entry.getCreatedBy(), entry.getId());
		});
		packagePropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(PackageProperty.class), entry.getCreatedBy(), entry.getId());
		});
		paymentRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Payment.class), entry.getCreatedBy(), entry.getId());
		});
		userPropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(UserProperty.class), entry.getCreatedBy(), entry.getId());
		});
		userRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(User.class), entry.getCreatedBy(), entry.getId());
		});
		warehousePropertyRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(WarehouseProperty.class), entry.getCreatedBy(), entry.getId());
		});
		warehouseRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(Warehouse.class), entry.getCreatedBy(), entry.getId());
		});
		defaultSharingRightsRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(DefaultSharingRights.class), entry.getCreatedBy(), entry.getId());
		});
		chatEntryRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(ChatEntry.class), entry.getCreatedBy(), entry.getId());
		});
		companyAddressRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CompanyAddress.class), entry.getCreatedBy(), entry.getId().getAddressId());
		});
		companyFavoriteRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(CompanyFavorite.class), entry.getCreatedBy(), entry.getId());
		});
		orderCommentChatRepository.findAll().forEach(entry -> {
			updateRights(FieldUtils.getAllFields(OrderCommentChat.class), entry.getCreatedBy(), entry.getId());
		});
		log.error("Stop update Rights");
	}
	
	private void updateRights(Field[] allFields, String keycloakEmail, UUID entityId) {
		AtomicBoolean haveToUpdate = new AtomicBoolean(false);
		var properties = new ArrayList<>(Arrays.stream(allFields)
				.map(Field::getName).toList());
		var propertiesCopy = new ArrayList<>(Arrays.stream(allFields)
				.map(Field::getName).toList());
		var compIdTemp = getUserId(keycloakEmail);
		if (compIdTemp.isPresent()) {
			try {
				var rights = rightsService.getAccess(entityId, compIdTemp.get(), compIdTemp.get());
				rights.ifPresent(rightsDTOCoreInternal -> {
					var props = rightsDTOCoreInternal.getObjectProperties();
					properties.retainAll(props.getReadProperties());
					if (!properties.isEmpty()) {
						haveToUpdate.set(true);
					}
					if (haveToUpdate.get()) {
						objectService.updateObject(entityId, rights.get().getObjectEntityClass(), compIdTemp.get(), new HashSet<>(propertiesCopy));
					}
				});
			} catch (Exception ignored) {
			
			}
		}
	}
	
	
	public Optional<UUID> getUserId(String createdBy) {
		try {
			var tempUsers = userRepository.findAllByKeycloakEmail(createdBy);
			AtomicReference<User> tempUser = new AtomicReference<>(new User());
			AtomicReference<OffsetDateTime> tempUserCreate = new AtomicReference<>(OffsetDateTime.MIN);
			tempUsers.forEach(user -> {
				if (tempUser.get().getId() != null) {
					if (tempUser.get().getCreateDate().isAfter(tempUserCreate.get())) {
						tempUser.set(user);
						tempUserCreate.set(user.getCreateDate());
					}
				} else {
					tempUser.set(user);
					tempUserCreate.set(user.getCreateDate());
				}
			});
			if (tempUser.get().getId() == null) {
				return Optional.empty();
			}
			return Optional.of(tempUser.get().getCompany().getId());
		} catch (Exception ignored) {
			return Optional.empty();
		}
	}
}
