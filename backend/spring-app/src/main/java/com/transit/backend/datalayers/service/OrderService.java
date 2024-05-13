package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.controller.dto.*;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService extends CrudServiceExtend<Order, UUID> {
	
	
	public Order filterExtend(Order order);
	
	
	public Order createInternal(Order entity, boolean root, UUID parentId);
	
	public String updateOrderStatus(UUID orderId, String status) throws MessagingException, ClassNotFoundException, InterruptedException;
	
	public String readOrderStatus(UUID orderId);
	
	
	Order saveOrder(Order order);
	
	Order copyCanceledOrder(UUID id);
	
	Optional<Order> resetOutsource(UUID orderId);
	
}
