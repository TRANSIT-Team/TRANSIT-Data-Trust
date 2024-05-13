package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class CompanyDeliveryAreaFilter extends AbstractEntityFilter<CompanyDeliveryArea, CompanyDeliveryArea> implements EntityFilterHelper<CompanyDeliveryArea, CompanyDeliveryArea> {
	
	
	@Override
	public CompanyDeliveryArea transformToTransfer(CompanyDeliveryArea entity) {
		return entity;
	}
	
	@Override
	public CompanyDeliveryArea transformToEntity(CompanyDeliveryArea entity) {
		return entity;
	}
	
	@Override
	public CompanyDeliveryArea transformToTransfer(CompanyDeliveryArea entity, CompanyDeliveryArea entityOld) {
		return entity;
	}
	
	@Override
	public Class<CompanyDeliveryArea> getClazz() {
		return CompanyDeliveryArea.class;
	}
	
	@Override
	public String getPathToEntity(CompanyDeliveryArea entity, CompanyDeliveryArea entity2) {
		return "/companies/" + entity.getCompany().getId() + "/deliveryarea/" + entity.getId();
	}
	
}
