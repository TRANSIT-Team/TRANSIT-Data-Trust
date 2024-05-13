package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.enums.ShowOverview;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.rightlayers.service.RightsManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.transit.backend.rightlayers.service.helper.DefaultListHelper.getEmptyList;

@Component
public class MethodXAddress {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderService orderService;
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Address methodXAddress(UUID addressId, UUID orderId) throws MessagingException, ClassNotFoundException, InterruptedException {
		var originalAddress = addressRepository.findById(addressId);
		var clonedAddress = originalAddress.get().clone();
		clonedAddress.setShowOverviewFilter(ShowOverview.NOTSHOW);
		clonedAddress = addressRepository.saveAndFlush(clonedAddress);
		var order = orderRepository.findById(orderId).get();
		if (order.getAddressFrom() != null && order.getAddressFrom().getId().equals(addressId)) {
			order.setAddressFrom(clonedAddress);
		}
		if (order.getAddressTo() != null && order.getAddressTo().getId().equals(addressId)) {
			order.setAddressTo(clonedAddress);
		}
		if (order.getAddressBilling() != null && order.getAddressBilling().getId().equals(addressId)) {
			order.setAddressBilling(clonedAddress);
		}
		var tempUsers = userRepository.findAllByKeycloakEmail(clonedAddress.getCreatedBy());
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
		rightsManageService.createEntityAndConnectIt(clonedAddress.getId(), Address.class.getSimpleName(), Address.class, tempUser.get().getId());
		TimeUnit.SECONDS.sleep(5);
		rightsManageService.connectEntityToNewCompany(clonedAddress.getId(), order.getCompany().getId(), orderId, getEmptyList(), getEmptyList(), false);
		orderService.update(orderId, order);
		return clonedAddress;
		
	}
}
