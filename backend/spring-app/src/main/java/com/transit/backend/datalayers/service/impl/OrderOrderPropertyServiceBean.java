package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.domain.QOrderProperty;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderPropertyRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.OrderOrderPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderPropertyMapper;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderOrderPropertyServiceBean extends CrudServiceSubRessourceAbstract<OrderProperty, OrderPropertyDTO, Order> implements OrderOrderPropertyService {
	
	@Autowired
	private OrderPropertyRepository orderPropertyRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderPropertyMapper orderPropertyMapper;
	
	@Override
	public AbstractRepository<OrderProperty> getPropertyRepository() {
		return this.orderPropertyRepository;
	}
	
	@Override
	public AbstractRepository<Order> getParentRepository() {
		return this.orderRepository;
	}
	
	@Override
	public AbstractMapper<OrderProperty, OrderPropertyDTO> getPropertyMapper() {
		return this.orderPropertyMapper;
	}
	
	@Override
	public Class<OrderProperty> getPropertyClazz() {
		return OrderProperty.class;
	}
	
	@Override
	public Class<OrderPropertyDTO> getPropertyDTOClazz() {
		return OrderPropertyDTO.class;
	}
	
	@Override
	public Class<Order> getParentClass() {
		return Order.class;
	}
	
	@Override
	public Path<OrderProperty> getPropertyQClazz() {
		return QOrderProperty.orderProperty;
	}
	
	@Override
	public String getParentString() {
		return "order";
	}
	
	@Override
	public OrderProperty create(UUID orderId, OrderProperty entity) {
		return super.createInternal(orderId, entity);
	}
	
	@Override
	public OrderProperty update(UUID orderId, UUID orderPropertyId, OrderProperty entity) {
		return super.updateInternal(orderId, orderPropertyId, entity);
	}
	
	@Override
	public OrderProperty partialUpdate(UUID orderId, UUID orderPropertyId, JsonMergePatch patch) {
		return super.partialUpdateInternal(orderId, orderPropertyId, patch);
	}
	
	@Override
	public Collection<OrderProperty> read(UUID orderId, String query, FilterExtra FilterExtra) {
		return super.readInternal(orderId, query, FilterExtra);
	}
	
	@Override
	public Optional<OrderProperty> readOne(UUID orderId, UUID orderPropertyId) {
		return super.readOneInternal(orderId, orderPropertyId);
	}
	
	@Override
	public void delete(UUID orderId, UUID orderPropertyId) {
		var entityToDelete = orderRepository.findById(orderId);
		if (entityToDelete.isPresent() && !(entityToDelete.get().getOrderStatus().equals(OrderStatus.OPEN))) {
			throw new UnprocessableEntityExeption("OrderProperty of Order in PROCESSING or COMPLETED cannot be deleted.");
		}
		super.deleteInternal(orderId, orderPropertyId);
	}
}
