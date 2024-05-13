package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CostFilter extends AbstractParentEntityFilter<Cost, Cost, CostProperty, CostProperty> implements EntityFilterHelper<Cost, Cost> {
	@Autowired
	private CostPropertyFilter costPropertyFilter;
	
	@Override
	public Cost transformToTransfer(Cost entity) {
		return entity;
	}
	
	@Override
	public Cost transformToEntity(Cost entity) {
		return entity;
	}
	
	@Override
	public Cost transformToTransfer(Cost entity, Cost entityOld) {
		return entity;
	}
	
	@Override
	public Class<Cost> getClazz() {
		return Cost.class;
	}
	
	@Override
	public String getPathToEntity(Cost entity, Cost entity2) {
		{
			return "/costs/" + entity.getId();
		}
	}
	
	@Override
	public AbstractPropertyEntityFilter<CostProperty, CostProperty, Cost, Cost> getPropertyFilter() {
		return costPropertyFilter;
	}
}
