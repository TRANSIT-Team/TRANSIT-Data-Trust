package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class CostPropertyFilter extends AbstractPropertyEntityFilter<CostProperty, CostProperty, Cost, Cost> implements EntityFilterHelper<CostProperty, CostProperty> {
	
	@Override
	public CostProperty transformToTransfer(CostProperty entity) {
		return entity;
	}
	
	@Override
	public CostProperty transformToEntity(CostProperty entity) {
		return entity;
	}
	
	@Override
	public CostProperty transformToTransfer(CostProperty entity, CostProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<CostProperty> getClazz() {
		return CostProperty.class;
	}
	
	@Override
	public String getPathToEntity(CostProperty entity, CostProperty entity2) {
		return "/costs/" + entity.getCost().getId() + "/costproperties/" + entity.getId();
	}
}
