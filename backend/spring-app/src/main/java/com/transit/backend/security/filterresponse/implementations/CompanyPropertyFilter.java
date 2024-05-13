package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyPropertyFilter extends AbstractPropertyEntityFilter<CompanyProperty, CompanyProperty, Company, Company> implements EntityFilterHelper<CompanyProperty, CompanyProperty> {
	@Override
	public CompanyProperty transformToTransfer(CompanyProperty entity) {
		return entity;
	}
	
	@Override
	public CompanyProperty transformToEntity(CompanyProperty entity) {
		return entity;
	}
	
	@Override
	public CompanyProperty transformToTransfer(CompanyProperty entity, CompanyProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<CompanyProperty> getClazz() {
		return CompanyProperty.class;
	}
	
	@Override
	public String getPathToEntity(CompanyProperty entity, CompanyProperty entity2) {
		return "/companies/" + entity.getCompany().getId() + "/companyproperties/" + entity.getId();
	}
	
	@Override
	public CompanyProperty filterEntities(CompanyProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
}