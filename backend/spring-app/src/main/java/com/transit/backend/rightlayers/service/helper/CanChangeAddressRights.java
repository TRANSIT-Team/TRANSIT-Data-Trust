package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CanChangeAddressRights {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Transactional(readOnly = true)
	public void canChangeAddressRights(Address address, Order suborder, AtomicBoolean canChange) {
		try (
				var orderStream = orderRepository.findOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(address.getId(), address.getId(), address.getId())
		) {
			orderStream.forEach(order -> {
				if (suborder.getId() != order.getId() &&
						suborder.getCompany().getId() == order.getCompany().getId() &&
						suborder.isSuborderType() &&
						order.isSuborderType()) {
					if (order.getOrderStatus().equals(OrderStatus.REQUESTED) ||
							order.getOrderStatus().equals(OrderStatus.ACCEPTED) ||
							order.getOrderStatus().equals(OrderStatus.OPEN) ||
							order.getOrderStatus().equals(OrderStatus.PROCESSING) ||
							order.getOrderStatus().equals(OrderStatus.COMPLETE) ||
							order.getOrderStatus().equals(OrderStatus.CANCELED)
					) {
						canChange.set(false);
					}
				}
				
			});
		}
	}
	
}
