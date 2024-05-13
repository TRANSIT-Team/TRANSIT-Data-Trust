package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderPropertyFilter extends AbstractPropertyEntityFilter<OrderProperty, OrderProperty, Order, Order> implements EntityFilterHelper<OrderProperty, OrderProperty> {
	@Override
	public OrderProperty transformToTransfer(OrderProperty entity) {
		return entity;
	}
	
	@Override
	public OrderProperty transformToEntity(OrderProperty entity) {
		return entity;
	}
	
	@Override
	public OrderProperty transformToTransfer(OrderProperty entity, OrderProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<OrderProperty> getClazz() {
		return OrderProperty.class;
	}
	
	@Override
	public String getPathToEntity(OrderProperty entity, OrderProperty entity2) {
		return "/orders/" + entity.getOrder().getId() + "/orderproperties/" + entity.getId();
	}
	
	@Override
	public OrderProperty filterEntities(OrderProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	
}