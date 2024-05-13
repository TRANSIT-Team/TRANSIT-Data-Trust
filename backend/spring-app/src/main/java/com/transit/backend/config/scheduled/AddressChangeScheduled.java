package com.transit.backend.config.scheduled;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyAddressId;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.enums.ShowOverview;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.service.impl.CompanyCompanyAddressServiceBean;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class AddressChangeScheduled {
	
	@Autowired
	private PingService pingService;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@Autowired
	private CompanyCustomerRepository companyCustomerRepository;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Scheduled(fixedDelay = 5000)
	public void updateAddresses() {
		try {
			pingService.available();
			//CompAddress and Order Address
			//Customer Address and Order Address
			//Company Address an Customer Address
			
			
			companyAddressRepository.findAllProjectedBy().forEach(entry -> {
				if (orderRepository.existsOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(entry.getId().getAddressId(), entry.getId().getAddressId(), entry.getId().getAddressId())) {
					var address = addressRepository.findById(entry.getId().getAddressId()).get();
					var newAddress = cloneAddressAndRights(address);
					
					
					var cAddress = companyAddressRepository.findByAddress_Id(address.getId()).get();
					
					companyAddressRepository.deleteById(new CompanyAddressId(entry.getId().getCompanyId(), entry.getId().getAddressId()));
					cAddress.setAddress(newAddress);
					cAddress.getId().setAddressId(newAddress.getId());
					companyAddressRepository.saveAndFlush(cAddress);
				}
			});
			companyCustomerRepository.findAll().forEach(entry -> {
				if (orderRepository.existsOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(entry.getAddressId(), entry.getAddressId(), entry.getAddressId())) {
					var address = addressRepository.findById(entry.getAddressId()).get();
					var newAddress = cloneAddressAndRights(address);
					entry.setAddressId(newAddress.getId());
					companyCustomerRepository.saveAndFlush(entry);
				}
			});
			companyAddressRepository.findAllProjectedBy().forEach(entry -> {
				if (companyCustomerRepository.existsByAddressId(entry.getId().getAddressId())) {
					var customers = companyCustomerRepository.findAllByAddressId(entry.getId().getAddressId());
					for (var cust : customers) {
						var address = addressRepository.findById(entry.getId().getAddressId()).get();
						var newAddress = cloneAddressAndRights(address);
						cust.setAddressId(newAddress.getId());
						companyCustomerRepository.saveAndFlush(cust);
					}
				}
			});
			
			
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public Address cloneAddressAndRights(Address address) {
		var newAddress = address.clone();
		newAddress.setShowOverviewFilter(ShowOverview.NOTSHOW);
		newAddress = addressRepository.saveAndFlush(newAddress);
		var tempUsers = userRepository.findAllByKeycloakEmail(newAddress.getCreatedBy());
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
		rightsManageService.createEntityAndConnectIt(newAddress.getId(), Address.class.getSimpleName(), Address.class, tempUser.get().getId());
		return newAddress;
	}
}
