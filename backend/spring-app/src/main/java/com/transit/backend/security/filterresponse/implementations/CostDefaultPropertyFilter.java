package com.transit.backend.security.filterresponse.implementations;


import com.transit.backend.datalayers.domain.CostDefaultProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CostDefaultPropertyFilter extends AbstractEntityFilter<CostDefaultProperty, CostDefaultProperty> implements EntityFilterHelper<CostDefaultProperty, CostDefaultProperty> {
	
	@Override
	public CostDefaultProperty transformToTransfer(CostDefaultProperty entity) {
		return entity;
	}
	
	@Override
	public CostDefaultProperty filterEntities(CostDefaultProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		//Company wird nicht mit übertragen
		return entity;
	}
	
	@Override
	public CostDefaultProperty transformToEntity(CostDefaultProperty entity) {
		return entity;
	}
	
	@Override
	public CostDefaultProperty transformToTransfer(CostDefaultProperty entity, CostDefaultProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<CostDefaultProperty> getClazz() {
		return CostDefaultProperty.class;
	}
	
	@Override
	public String getPathToEntity(CostDefaultProperty entity, CostDefaultProperty entity2) {
		return "/costproperties/" + entity.getId();
	}
}
