package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.DeliveryMethod;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeliveryMethodFilter extends AbstractEntityFilter<DeliveryMethod, DeliveryMethod> implements EntityFilterHelper<DeliveryMethod, DeliveryMethod> {
	
	@Override
	public DeliveryMethod transformToTransfer(DeliveryMethod entity) {
		return entity;
	}
	
	@Override
	public DeliveryMethod filterEntities(DeliveryMethod entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public DeliveryMethod transformToEntity(DeliveryMethod entity) {
		return entity;
	}
	
	@Override
	public DeliveryMethod transformToTransfer(DeliveryMethod entity, DeliveryMethod entityOld) {
		return entity;
	}
	
	@Override
	public Class<DeliveryMethod> getClazz() {
		return DeliveryMethod.class;
	}
	
	@Override
	public String getPathToEntity(DeliveryMethod entity, DeliveryMethod entity2) {
		return "/deliverymethods/" + entity.getId();
	}
}
