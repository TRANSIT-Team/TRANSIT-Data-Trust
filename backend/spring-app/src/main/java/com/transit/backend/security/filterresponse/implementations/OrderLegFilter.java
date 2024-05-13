package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderLegFilter extends AbstractEntityFilter<OrderLeg, OrderLeg> implements EntityFilterHelper<OrderLeg, OrderLeg> {
	@Override
	public OrderLeg transformToTransfer(OrderLeg entity) {
		return entity;
	}
	
	@Override
	public OrderLeg filterEntities(OrderLeg entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public OrderLeg transformToEntity(OrderLeg entity) {
		return entity;
	}
	
	@Override
	public OrderLeg transformToTransfer(OrderLeg entity, OrderLeg entityOld) {
		return entity;
	}
	
	@Override
	public Class<OrderLeg> getClazz() {
		return OrderLeg.class;
	}
	
	@Override
	public String getPathToEntity(OrderLeg entity, OrderLeg entity2) {
		return "/orderlegs/" + entity.getId();
	}
}
