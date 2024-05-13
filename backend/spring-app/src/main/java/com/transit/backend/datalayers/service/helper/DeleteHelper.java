package com.transit.backend.datalayers.service.helper;

import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.IdentityService;
import com.transit.backend.rightlayers.service.ObjectService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class DeleteHelper {
	
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private UserPropertyRepository userPropertyRepository;
	@Autowired
	private ObjectService objectService;
	
	@Autowired
	KeycloakServiceManager keycloakServiceManager;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private AccessService accessService;
	
	
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CarPropertyRepository carPropertyRepository;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private CompanyDeliveryAreaRepository companyDeliveryAreaRepository;
	
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	
	@Autowired
	private DeliveryMethodRepository deliveryMethodRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private OrderPropertyRepository orderPropertyRepository;
	
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
	private WarehousePropertyRepository warehousePropertyRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	
	@Autowired
	private CompanyFavoriteRepository companyFavoriteRepository;
	@Autowired
	private OrderCommentChatRepository orderCommentChatRepository;
	@Autowired
	private ChatEntryRepository chatEntryRepository;
	@Autowired
	private CompanyCustomerRepository companyCustomerRepository;
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	@Autowired
	private ContactPersonRepository contactPersonRepository;
	@Autowired
	private CostDefaultPropertyRepository costDefaultPropertyRepository;
	@Autowired
	private CostPropertyRepository costPropertyRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private DefaultSharingRightsRepository defaultSharingRightsRepository;
	@Autowired
	private PaymentPropertyRepository paymentPropertyRepository;
	
	@Transactional
	
	public void deleteUser(UUID id) {
		var compId = userHelperFunctions.getCompanyId(id);
		var userToDelete = userRepository.findById(id).get();
		if (userRepository.findAllByCompanyId(userHelperFunctions.getCompanyId(id)).size() > 1) {
			
			userToDelete.getProperties().forEach(entry -> {
				try {
					userPropertyRepository.deleteById(entry.getId());
				} catch (Exception e) {
					try {
						entry.setDeleted(true);
						userPropertyRepository.saveAndFlush(entry);
					} catch (Exception ex) {
					
					}
				}
				objectService.deleteObject(compId, entry.getId());
			});
			try {
				userRepository.deleteById(userToDelete.getId());
			} catch (Exception e) {
				try {
					userToDelete.setDeleted(true);
					userRepository.saveAndFlush(userToDelete);
				} catch (Exception ex) {
				
				}
			}
			objectService.deleteObject(compId, userToDelete.getId());
			keycloakServiceManager.getUsersResource().delete(userToDelete.getKeycloakId().toString());
		} else {
			AtomicBoolean canDelete = new AtomicBoolean(true);
			orderRepository.findAllByCompanyId(compId).forEach(order -> {
						if (order.getSuborders() != null && !order.getSuborders().isEmpty()) {
							order.getSuborders().forEach(entry -> {
								var status = entry.getOrderStatus();
								if (status.equals(OrderStatus.OPEN) ||
										status.equals(OrderStatus.REQUESTED) ||
										status.equals(OrderStatus.PROCESSING)) {
									canDelete.set(false);
								}
							});
						}
					}
			);
			if (!canDelete.get()) {
				throw new BadRequestException("There are still orders with status OPEN, REQUESTED, PROCESSING.");
			}
			Set<UUID> entriesToDelete = new HashSet<>();
			
			collectIds(Car.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CarProperty.class.getSimpleName(), compId, entriesToDelete);
			
			collectIds(Warehouse.class.getSimpleName(), compId, entriesToDelete);
			collectIds(WarehouseProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(Cost.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CostProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(Payment.class.getSimpleName(), compId, entriesToDelete);
			collectIds(PaymentProperty.class.getSimpleName(), compId, entriesToDelete);
			
			collectIds(DeliveryMethod.class.getSimpleName(), compId, entriesToDelete);
			collectIds(Order.class.getSimpleName(), compId, entriesToDelete);
			collectIds(ChatEntry.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderLeg.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderRoute.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderType.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderTypeProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(OrderCommentChat.class.getSimpleName(), compId, entriesToDelete);
			
			
			collectIds(PackageItem.class.getSimpleName(), compId, entriesToDelete);
			collectIds(PackagePackageProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(PackageProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(PackageRoute.class.getSimpleName(), compId, entriesToDelete);
			collectIds(PackageTracking.class.getSimpleName(), compId, entriesToDelete);
			
			
			collectIds(Company.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CompanyProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CompanyDeliveryArea.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CompanyFavorite.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CompanyIDToCompanyOID.class.getSimpleName(), compId, entriesToDelete);
			collectIds(ContactPerson.class.getSimpleName(), compId, entriesToDelete);
			collectIds(User.class.getSimpleName(), compId, entriesToDelete);
			collectIds(UserProperty.class.getSimpleName(), compId, entriesToDelete);
			collectIds(DefaultSharingRights.class.getSimpleName(), compId, entriesToDelete);
			collectIds(Location.class.getSimpleName(), compId, entriesToDelete);
			collectIds(CompanyAddress.class.getSimpleName(), compId, entriesToDelete);
			collectIds(Address.class.getSimpleName(), compId, entriesToDelete);
			
			
			collectIds(Customer.class.getSimpleName(), compId, entriesToDelete);
			
			
			deleteClass(Car.class.getSimpleName(), compId);
			deleteClass(CarProperty.class.getSimpleName(), compId);
			
			deleteClass(Warehouse.class.getSimpleName(), compId);
			deleteClass(WarehouseProperty.class.getSimpleName(), compId);
			deleteClass(Cost.class.getSimpleName(), compId);
			deleteClass(CostProperty.class.getSimpleName(), compId);
			deleteClass(Payment.class.getSimpleName(), compId);
			deleteClass(PaymentProperty.class.getSimpleName(), compId);
			
			deleteClass(DeliveryMethod.class.getSimpleName(), compId);
			deleteClass(Order.class.getSimpleName(), compId);
			deleteClass(ChatEntry.class.getSimpleName(), compId);
			deleteClass(OrderLeg.class.getSimpleName(), compId);
			deleteClass(OrderProperty.class.getSimpleName(), compId);
			deleteClass(OrderRoute.class.getSimpleName(), compId);
			deleteClass(OrderType.class.getSimpleName(), compId);
			deleteClass(OrderTypeProperty.class.getSimpleName(), compId);
			deleteClass(OrderCommentChat.class.getSimpleName(), compId);
			
			
			deleteClass(PackageItem.class.getSimpleName(), compId);
			deleteClass(PackagePackageProperty.class.getSimpleName(), compId);
			deleteClass(PackageProperty.class.getSimpleName(), compId);
			deleteClass(PackageRoute.class.getSimpleName(), compId);
			deleteClass(PackageTracking.class.getSimpleName(), compId);
			
			deleteClass(CompanyDeliveryArea.class.getSimpleName(), compId);
			deleteClass(Company.class.getSimpleName(), compId);
			deleteClass(CompanyProperty.class.getSimpleName(), compId);
			deleteClass(CompanyFavorite.class.getSimpleName(), compId);
			deleteClass(CompanyIDToCompanyOID.class.getSimpleName(), compId);
			deleteClass(ContactPerson.class.getSimpleName(), compId);
			deleteClass(User.class.getSimpleName(), compId);
			deleteClass(UserProperty.class.getSimpleName(), compId);
			deleteClass(DefaultSharingRights.class.getSimpleName(), compId);
			deleteClass(Location.class.getSimpleName(), compId);
			deleteClass(CompanyAddress.class.getSimpleName(), compId);
			deleteClass(Address.class.getSimpleName(), compId);
			
			
			deleteClass(Customer.class.getSimpleName(), compId);
			
			deleteClassRightsIfExists(entriesToDelete, compId);
			
			//Will delete also all Sub Rights;
			//identityService.deleteIdentity(compId);
			keycloakServiceManager.getUsersResource().delete(userToDelete.getKeycloakId().toString());
		}
		
	}
	
	
	public void collectIds(String clazz, UUID companyId, Set<UUID> entriesToDelete) {
		boolean changeAddressType;
		if (clazz.equals(CompanyAddress.class.getSimpleName())) {
			clazz = Address.class.getSimpleName();
			changeAddressType = true;
		} else {
			changeAddressType = false;
		}
		var entities = accessService.getAccessClazz(clazz, true, companyId);
		entities.getObjects().forEach(entry -> {
			
			var uuid = entry.getObjectId();
			
			var response5 = companyAddressRepository.findByAddress_Id(uuid);
			if (response5.isPresent()) {
				entriesToDelete.add(uuid);
			}
			
			if (!changeAddressType) {
				var response = addressRepository.findById(uuid);
				if (response.isPresent()) {
					AtomicBoolean del = new AtomicBoolean(true);
					
					orderRepository.findOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(uuid, uuid, uuid).forEach(order -> {
						if (!order.getCompany().getId().equals(companyId)) {
							//not all
							del.set(false);
						}
					});
					if (del.get()) {
						entriesToDelete.add(uuid);
					}
					
				}
			}
			var response2 = carPropertyRepository.findById(uuid);
			if (response2.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response3 = carRepository.findById(uuid);
			if (response3.isPresent()) {
				if (response3.get().getLocations() != null && !response3.get().getLocations().isEmpty()) {
					response3.get().getLocations().forEach(loc -> {
						entriesToDelete.add(loc.getId());
						
					});
					
				}
				
				entriesToDelete.add(uuid);
			}
			
			var response6 = companyCustomerRepository.findById(uuid);
			if (response6.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response7 = companyDeliveryAreaRepository.findById(uuid);
			if (response7.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response8 = companyFavoriteRepository.findById(uuid);
			if (response8.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response9 = companyIdToCompanyOIDRepository.findById(uuid);
			if (response9.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response10 = companyPropertyRepository.findById(uuid);
			if (response10.isPresent()) {
				entriesToDelete.add(uuid);
			}
			
			//have to parse from companyOID
			var response11 = companyRepository.findById(uuid);
			if (response11.isPresent()) {
				entriesToDelete.add(companyIdToCompanyOIDRepository.findById(uuid).get().getCompanyOID());
				
			}
			var temp = companyIdToCompanyOIDRepository.findCompanyIDToCompanyOIDByCompanyOID(uuid);
			if (temp.isPresent()) {
				response11 = companyRepository.findById(companyIdToCompanyOIDRepository.findCompanyIDToCompanyOIDByCompanyOID(uuid).get().getCompanyId());
				if (response11.isPresent()) {
					entriesToDelete.add((uuid));
				}
			}
			var response12 = contactPersonRepository.findById(uuid);
			if (response12.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response13 = costDefaultPropertyRepository.findById(uuid);
			if (response13.isPresent()) {
				entriesToDelete.add(uuid);
			}
			
			var response15 = costPropertyRepository.findById(uuid);
			if (response15.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response16 = costRepository.findById(uuid);
			if (response16.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response17 = defaultSharingRightsRepository.findById(uuid);
			if (response17.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response18 = deliveryMethodRepository.findById(uuid);
			if (response18.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response19 = locationRepository.findById(uuid);
			if (response19.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response20 = orderCommentChatRepository.findById(uuid);
			if (response20.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response21 = orderPropertyRepository.findById(uuid);
			if (response21.isPresent()) {
				boolean del = true;
				if (!orderRepository.findById(response21.get().getOrder().getId()).get().getCompany().getId().equals(companyId)) {
					//not all
					del = false;
				}
				if (del) {
					entriesToDelete.add(uuid);
				}
			}
			var response22 = orderRepository.findById(uuid);
			if (response22.isPresent()) {
				boolean del = true;
				if (!response22.get().getCompany().getId().equals(companyId)) {
					//not all
					del = false;
				}
				if (del) {
					var response4 = chatEntryRepository.findChatEntriesByOrderIdOrderBySequenceId(uuid);
					response4.forEach(chatEntry -> {
						entriesToDelete.add(chatEntry.getId());
					});
					if (response22.get().getSuborders().isEmpty()) {
						response22.get().getPackageItems().forEach(pack -> {
							pack.getPackagePackageProperties().forEach(prop -> {
								
								entriesToDelete.add(prop.getId());
							});
							
							entriesToDelete.add(pack.getId());
						});
					}
					entriesToDelete.add(uuid);
				}
			}
			
			var response23 = orderRouteRepository.findById(uuid);
			if (response23.isPresent()) {
				entriesToDelete.add(uuid);
			}
			
			var response24 = orderTypePropertyRepository.findById(uuid);
			if (response24.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response25 = orderTypeRepository.findById(uuid);
			if (response25.isPresent()) {
				entriesToDelete.add(uuid);
			}
			
			var response28 = packagePropertyRepository.findById(uuid);
			if (response28.isPresent()) {
				
				entriesToDelete.add(uuid);
			}
			var response29 = paymentPropertyRepository.findById(uuid);
			if (response29.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response30 = paymentRepository.findById(uuid);
			if (response30.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response31 = userPropertyRepository.findById(uuid);
			if (response31.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response32 = userRepository.findById(uuid);
			if (response32.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response33 = warehousePropertyRepository.findById(uuid);
			if (response33.isPresent()) {
				entriesToDelete.add(uuid);
			}
			var response34 = warehouseRepository.findById(uuid);
			if (response34.isPresent()) {
				entriesToDelete.add(uuid);
			}
		});
	}
	
	
	public void deleteClass(String clazz, UUID companyId) {
		boolean changeAddressType;
		if (clazz.equals(CompanyAddress.class.getSimpleName())) {
			clazz = Address.class.getSimpleName();
			changeAddressType = true;
		} else {
			changeAddressType = false;
		}
		var entities = accessService.getAccessClazz(clazz, true, companyId);
		entities.getObjects().forEach(entry -> {
			
			var uuid = entry.getObjectId();
			
			var response5 = companyAddressRepository.findByAddress_Id(uuid);
			if (response5.isPresent()) {
				try {
					companyAddressRepository.deleteById(new CompanyAddressId(companyId, uuid));
				} catch (Exception e) {
				
				}
			}
			
			if (!changeAddressType) {
				var response = addressRepository.findById(uuid);
				if (response.isPresent()) {
					AtomicBoolean del = new AtomicBoolean(true);
					
					orderRepository.findOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(uuid, uuid, uuid).forEach(order -> {
						if (!order.getCompany().getId().equals(companyId)) {
							//not all
							del.set(false);
						}
					});
					if (del.get()) {
						try {
							addressRepository.deleteById(uuid);
						} catch (Exception e) {
							try {
								response.get().setDeleted(true);
								addressRepository.saveAndFlush(response.get());
							} catch (Exception ex) {
							
							}
						}
					}
					
				}
			}
			var response2 = carPropertyRepository.findById(uuid);
			if (response2.isPresent()) {
				try {
					carPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response2.get().setDeleted(true);
						carPropertyRepository.saveAndFlush(response2.get());
					} catch (Exception ex) {
						
					}
				}
			}
			var response3 = carRepository.findById(uuid);
			if (response3.isPresent()) {
				if (response3.get().getLocations() != null && !response3.get().getLocations().isEmpty()) {
					response3.get().getLocations().forEach(loc -> {
						try {
							locationRepository.deleteById(loc.getId());
						} catch (Exception e) {
							try {
								loc.setDeleted(true);
								locationRepository.saveAndFlush(loc);
							} catch (Exception ex) {
							
							}
						}
						
						
					});
					
				}
				try {
					carRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response3.get().setDeleted(true);
						carRepository.saveAndFlush(response3.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			
			var response6 = companyCustomerRepository.findById(uuid);
			if (response6.isPresent()) {
				try {
					companyCustomerRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response6.get().setDeleted(true);
						companyCustomerRepository.saveAndFlush(response6.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response7 = companyDeliveryAreaRepository.findById(uuid);
			if (response7.isPresent()) {
				response7.get().setDeliveryAreaZips(null);
				response7.get().getDeliveryAreaPolyline();
				response7.get().setDeliveryAreaPolyline(null);
				companyDeliveryAreaRepository.saveAndFlush(response7.get());
				try {
					companyDeliveryAreaRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response7.get().setDeleted(true);
						companyDeliveryAreaRepository.saveAndFlush(response7.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response8 = companyFavoriteRepository.findById(uuid);
			if (response8.isPresent()) {
				response8.get().setCompanyList(null);
				companyFavoriteRepository.saveAndFlush(response8.get());
				try {
					companyFavoriteRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response8.get().setDeleted(true);
						companyFavoriteRepository.saveAndFlush(response8.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			var response9 = companyIdToCompanyOIDRepository.findById(uuid);
			if (response9.isPresent()) {
				var companyOID = response9.get().getCompanyOID();
				try {
					companyIdToCompanyOIDRepository.deleteById(uuid);
				} catch (Exception e) {
				}
				
			}
			var response10 = companyPropertyRepository.findById(uuid);
			if (response10.isPresent()) {
				try {
					companyPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response10.get().setDeleted(true);
						companyPropertyRepository.saveAndFlush(response10.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			var response11 = companyRepository.findById(uuid);
			if (response11.isPresent()) {
				try {
					companyRepository.deleteById(uuid);
					defaultSharingRightsRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response11.get().setDeleted(true);
						companyRepository.saveAndFlush(response11.get());
						var temp = defaultSharingRightsRepository.getReferenceById(uuid);
						temp.setDeleted(true);
						defaultSharingRightsRepository.saveAndFlush(temp);
					} catch (Exception ex) {
					
					}
				}
			}
			var temp = companyIdToCompanyOIDRepository.findCompanyIDToCompanyOIDByCompanyOID(uuid);
			if (temp.isPresent()) {
				response11 = companyRepository.findById(companyIdToCompanyOIDRepository.findCompanyIDToCompanyOIDByCompanyOID(uuid).get().getCompanyId());
				if (response11.isPresent()) {
					try {
						companyRepository.deleteById(response11.get().getId());
						defaultSharingRightsRepository.deleteById(response11.get().getId());
					} catch (Exception e) {
						try {
							
							response11.get().setDeleted(true);
							companyRepository.saveAndFlush(response11.get());
							var temp2 = defaultSharingRightsRepository.getReferenceById(uuid);
							temp2.setDeleted(true);
							defaultSharingRightsRepository.saveAndFlush(temp2);
						} catch (Exception ex) {
						
						}
					}
				}
			}
			var response12 = contactPersonRepository.findById(uuid);
			if (response12.isPresent()) {
				try {
					
					contactPersonRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response12.get().setDeleted(true);
						contactPersonRepository.saveAndFlush(response12.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			var response13 = costDefaultPropertyRepository.findById(uuid);
			if (response13.isPresent()) {
				try {
					
					costDefaultPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response13.get().setDeleted(true);
						costDefaultPropertyRepository.saveAndFlush(response13.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			
			var response15 = costPropertyRepository.findById(uuid);
			if (response15.isPresent()) {
				try {
					
					costPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response15.get().setDeleted(true);
						costPropertyRepository.saveAndFlush(response15.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			var response16 = costRepository.findById(uuid);
			if (response16.isPresent()) {
				try {
					
					costRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response16.get().setDeleted(true);
						costRepository.saveAndFlush(response16.get());
					} catch (Exception ex) {
					
					}
				}
				
			}
			var response17 = defaultSharingRightsRepository.findById(uuid);
			if (response17.isPresent()) {
				try {
					
					defaultSharingRightsRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response17.get().setDeleted(true);
						defaultSharingRightsRepository.saveAndFlush(response17.get());
					} catch (Exception ex) {
					
					}
					
				}
			}
			
			
			var response18 = deliveryMethodRepository.findById(uuid);
			if (response18.isPresent()) {
				try {
					
					deliveryMethodRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response18.get().setDeleted(true);
						deliveryMethodRepository.saveAndFlush(response18.get());
					} catch (Exception ex) {
					
					}
				}
			}
			var response19 = locationRepository.findById(uuid);
			if (response19.isPresent()) {
				
				try {
					locationRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response19.get().setDeleted(true);
						locationRepository.saveAndFlush(response19.get());
					} catch (Exception ex) {
					
					}
					
				}
			}
			var response20 = orderCommentChatRepository.findById(uuid);
			if (response20.isPresent()) {
				
				try {
					orderCommentChatRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response20.get().setDeleted(true);
						orderCommentChatRepository.saveAndFlush(response20.get());
					} catch (Exception ex) {
						
					}
					
				}
				var response21 = orderPropertyRepository.findById(uuid);
				if (response21.isPresent()) {
					boolean del = true;
					if (!orderRepository.findById(response21.get().getOrder().getId()).get().getCompany().getId().equals(companyId)) {
						//not all
						del = false;
					}
					if (del) {
						try {
							orderPropertyRepository.deleteById(uuid);
						} catch (Exception e) {
							try {
								response21.get().setDeleted(true);
								orderPropertyRepository.saveAndFlush(response21.get());
							} catch (Exception ex) {
								
							}
						}
					} else {
						try {
							response21.get().setDeleted(true);
							orderPropertyRepository.saveAndFlush(response21.get());
						} catch (Exception e) {
						}
					}
				}
			}
			var response22 = orderRepository.findById(uuid);
			if (response22.isPresent()) {
				boolean del = true;
				if (!response22.get().getCompany().getId().equals(companyId)) {
					//not all
					del = false;
				}
				if (del) {
					var response4 = chatEntryRepository.findChatEntriesByOrderIdOrderBySequenceId(uuid);
					response4.forEach(chatEntry -> {
						
						try {
							chatEntryRepository.deleteById(chatEntry.getId());
						} catch (Exception e) {
							try {
								chatEntry.setDeleted(true);
								chatEntryRepository.saveAndFlush(chatEntry);
							} catch (Exception ex) {
								
							}
						}
					});
					if (!response22.get().getSuborders().isEmpty()) {
						response22.get().getSuborders().forEach(sub -> {
							sub.setParentOrder(null);
							orderRepository.saveAndFlush(sub);
						});
						
						response22.get().setPackageItems(null);
						orderRepository.saveAndFlush(response22.get());
						
						try {
							response22.get().setCompany(null);
							orderRepository.saveAndFlush(response22.get());
						} catch (Exception e) {
							
						}
						
					} else {
						response22.get().getPackageItems().forEach(pack -> {
							pack.getPackagePackageProperties().forEach(prop -> {
								try {
									packageItemPackagePropertyRepository.deleteById(prop.getId());
								} catch (Exception e) {
									try {
										prop.setDeleted(true);
										packageItemPackagePropertyRepository.saveAndFlush(prop);
									} catch (Exception ex) {
										
									}
								}
								
							});
							try {
								packageItemRepository.deleteById(pack.getId());
							} catch (Exception e) {
								try {
									pack.setDeleted(true);
									packageItemRepository.saveAndFlush(pack);
								} catch (Exception ex) {
									
								}
							}
							
						});
					}
					try {
						orderRepository.deleteById(uuid);
					} catch (Exception e) {
						try {
							response22.get().setDeleted(true);
							orderRepository.saveAndFlush(response22.get());
						} catch (Exception ex) {
							
						}
					}
					
				}
			}
			
			var response23 = orderRouteRepository.findById(uuid);
			if (response23.isPresent()) {
				try {
					orderRouteRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response23.get().setDeleted(true);
						orderRouteRepository.saveAndFlush(response23.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			
			var response24 = orderTypePropertyRepository.findById(uuid);
			if (response24.isPresent()) {
				try {
					
					orderTypePropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response24.get().setDeleted(true);
						orderTypePropertyRepository.saveAndFlush(response24.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response25 = orderTypeRepository.findById(uuid);
			if (response25.isPresent()) {
				try {
					
					orderTypeRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response25.get().setDeleted(true);
						orderTypeRepository.saveAndFlush(response25.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			
			var response28 = packagePropertyRepository.findById(uuid);
			if (response28.isPresent()) {
				try {
					packagePropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response28.get().setDeleted(true);
						packagePropertyRepository.saveAndFlush(response28.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response29 = paymentPropertyRepository.findById(uuid);
			if (response29.isPresent()) {
				try {
					
					paymentPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response29.get().setDeleted(true);
						paymentPropertyRepository.saveAndFlush(response29.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response30 = paymentRepository.findById(uuid);
			if (response30.isPresent()) {
				
				try {
					paymentRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response30.get().setDeleted(true);
						paymentRepository.saveAndFlush(response30.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response31 = userPropertyRepository.findById(uuid);
			if (response31.isPresent()) {
				try {
					
					userPropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response31.get().setDeleted(true);
						userPropertyRepository.saveAndFlush(response31.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response32 = userRepository.findById(uuid);
			if (response32.isPresent()) {
				try {
					
					userRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response32.get().setDeleted(true);
						userRepository.saveAndFlush(response32.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response33 = warehousePropertyRepository.findById(uuid);
			if (response33.isPresent()) {
				
				try {
					warehousePropertyRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response33.get().setDeleted(true);
						warehousePropertyRepository.saveAndFlush(response33.get());
					} catch (Exception ex) {
						
					}
				}
				
			}
			var response34 = warehouseRepository.findById(uuid);
			if (response34.isPresent()) {
				
				try {
					warehouseRepository.deleteById(uuid);
				} catch (Exception e) {
					try {
						response34.get().setDeleted(true);
						warehouseRepository.saveAndFlush(response34.get());
					} catch (Exception ex) {
						
					}
				}
			}
			
			
		});
	}
	
	
	private void deleteClassRightsIfExists(Set<UUID> entriesToDelete, UUID companyId) {
		for (var x : entriesToDelete) {
			try {
				objectService.deleteObject(companyId, x);
			} catch (Exception ignored) {
				log.error(ignored.getMessage());
				ignored.printStackTrace();
			}
		}
	}
}
