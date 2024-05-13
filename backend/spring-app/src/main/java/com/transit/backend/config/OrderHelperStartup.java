package com.transit.backend.config;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderHelperStartup {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderService orderService;
	
	@Transactional
	public List<Order> loadOrders() {
		var orders = orderRepository.findAll();
		orders.forEach(order -> Hibernate.initialize(order.getOrderProperties()));
		
		return orders;
	}
	
	@Transactional
	public void saveOrder(Order o) {
		o.getOrderProperties();
		if (o.getIdString() == null) {
			o.setIdString(o.getId().toString());
			orderService.saveOrder(o);
		}
	}
}
