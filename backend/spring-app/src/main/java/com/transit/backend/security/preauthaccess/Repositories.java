package com.transit.backend.security.preauthaccess;

import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class Repositories {
	
	List<AbstractRepository<AbstractEntity>> repositories;
	
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
	private CompanyRepository companyRepository;
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
	private CompanyFavoriteRepository companyFavoriteRepository;
	@Autowired
	private OrderCommentChatRepository orderCommentChatRepository;
	
	
	public boolean entityExists(UUID uuid) {
		var response = addressRepository.findById(uuid);
		if (response.isPresent()) {
			return true;
		}
		var response2 = carPropertyRepository.findById(uuid);
		if (response2.isPresent()) {
			return true;
		}
		var response3 = carRepository.findById(uuid);
		if (response3.isPresent()) {
			return true;
		}
		var response4 = carPropertyRepository.findById(uuid);
		if (response4.isPresent()) {
			return true;
		}
		var response5 = companyAddressRepository.findByAddress_Id(uuid);
		if (response5.isPresent()) {
			return true;
		}
		var response6 = companyDeliveryAreaRepository.findById(uuid);
		if (response6.isPresent()) {
			return true;
		}
		var response7 = companyPropertyRepository.findById(uuid);
		if (response7.isPresent()) {
			return true;
		}
		var response8 = companyRepository.findById(uuid);
		if (response8.isPresent()) {
			return true;
		}
		var response9 = deliveryMethodRepository.findById(uuid);
		if (response9.isPresent()) {
			return true;
		}
		var response10 = locationRepository.findById(uuid);
		if (response10.isPresent()) {
			return true;
		}
		var response11 = orderPropertyRepository.findById(uuid);
		if (response11.isPresent()) {
			return true;
		}
		var response12 = orderRepository.findById(uuid);
		if (response12.isPresent()) {
			return true;
		}
		var response13 = orderRouteRepository.findById(uuid);
		if (response13.isPresent()) {
			return true;
		}
		var response14 = orderRouteRepository.findById(uuid);
		if (response14.isPresent()) {
			return true;
		}
		var response15 = orderTypePropertyRepository.findById(uuid);
		if (response15.isPresent()) {
			return true;
		}
		var response16 = orderTypeRepository.findById(uuid);
		if (response16.isPresent()) {
			return true;
		}
		var response17 = packageClassRepository.findById(uuid);
		if (response17.isPresent()) {
			return true;
		}
		var response18 = packageItemPackagePropertyRepository.findById(uuid);
		if (response18.isPresent()) {
			return true;
		}
		var response19 = packageItemRepository.findById(uuid);
		if (response19.isPresent()) {
			return true;
		}
		var response20 = packagePropertyRepository.findById(uuid);
		if (response20.isPresent()) {
			return true;
		}
		var response21 = paymentRepository.findById(uuid);
		if (response21.isPresent()) {
			return true;
		}
		var response22 = userPropertyRepository.findById(uuid);
		if (response22.isPresent()) {
			return true;
		}
		
		var response23 = userRepository.findById(uuid);
		if (response23.isPresent()) {
			return true;
		}
		
		var response24 = warehousePropertyRepository.findById(uuid);
		if (response24.isPresent()) {
			return true;
		}
		var response25 = warehouseRepository.findById(uuid);
		if (response25.isPresent()) {
			return true;
		}
		var response26 = companyFavoriteRepository.findById(uuid);
		if (response26.isPresent()) {
			return true;
		}
		var response27 = orderCommentChatRepository.findById(uuid);
		if (response27.isPresent()) {
			return true;
		}
		
		
		return false;
		
	}
	
	public Optional<AbstractEntity> getEntity(UUID uuid) {
		for (AbstractRepository<AbstractEntity> repo : repositories) {
			Optional<AbstractEntity> response = repo.findById(uuid);
			if (response.isPresent()) {
				return response;
			}
		}
		var response = companyAddressRepository.findByAddress_Id(uuid);
		return response.map(CompanyAddress::getAddress);
		
	}
	
}
