package com.transit.backend.datalayers.controller.assembler.helper;

import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SetSuborderOrderFields {
	
	
	public OrderDTO setSuborderOrderSettings(OrderDTO dto, Order order) {
		if (order.getSuborders() != null && !order.getSuborders().isEmpty()) {
			Set<OrderStatus> collectedStatus = new HashSet<>();
			order.getSuborders().forEach(suborder -> collectedStatus.add(suborder.getOrderStatus()));
			//Exclusive Status
			if (collectedStatus.contains(OrderStatus.ACCEPTED) ||
					collectedStatus.contains(OrderStatus.REQUESTED) ||
					collectedStatus.contains(OrderStatus.OPEN) ||
					collectedStatus.contains(OrderStatus.PROCESSING) ||
					collectedStatus.contains(OrderStatus.COMPLETE) ||
					collectedStatus.contains(OrderStatus.CANCELED)) {
				dto.setHasNotableSuborderStatus(true);
				dto.setNotableSuborderStatus(getNotableOrderStatus(collectedStatus).toString());
			}
			//Non-exclusive but different from Revoked
			else if (collectedStatus.contains(OrderStatus.REJECTED)) {
				dto.setHasNotableSuborderStatus(true);
				dto.setNotableSuborderStatus(OrderStatus.REJECTED.toString());
			} else {
				dto.setHasNotableSuborderStatus(false);
				dto.setNotableSuborderStatus("");
			}
		} else {
			dto.setHasNotableSuborderStatus(false);
			dto.setNotableSuborderStatus("");
		}
		return dto;
	}
	
	private OrderStatus getNotableOrderStatus(Set<OrderStatus> collectedStatus) {
		if (collectedStatus.contains(OrderStatus.ACCEPTED)) {
			return OrderStatus.ACCEPTED;
		} else if (collectedStatus.contains(OrderStatus.REQUESTED)) {
			return OrderStatus.REQUESTED;
		} else if (collectedStatus.contains(OrderStatus.OPEN)) {
			return OrderStatus.OPEN;
		} else if (collectedStatus.contains(OrderStatus.PROCESSING)) {
			return OrderStatus.PROCESSING;
		} else if (collectedStatus.contains(OrderStatus.COMPLETE)) {
			return OrderStatus.COMPLETE;
		} else {
			return OrderStatus.CANCELED;
		}
		
	}
}
