package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyFilter extends AbstractParentEntityFilter<Company, Company, CompanyProperty, CompanyProperty> implements EntityFilterHelper<Company, Company> {
	
	@Autowired
	private CompanyPropertyFilter companyPropertyFilter;
	
	@Override
	public Company transformToTransfer(Company entity) {
		return entity;
	}
	
	@Override
	public Company transformToEntity(Company entity) {
		return entity;
	}
	
	@Override
	public Company transformToTransfer(Company entity, Company entityOld) {
		return entity;
	}
	
	@Override
	public Class<Company> getClazz() {
		return Company.class;
	}
	
	@Override
	public String getPathToEntity(Company entity, Company entity2) {
		return "/companies/" + entity.getId();
	}
	
	@Override
	public Company filterEntities(Company entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public AbstractPropertyEntityFilter<CompanyProperty, CompanyProperty, Company, Company> getPropertyFilter() {
		return this.companyPropertyFilter;
	}
}
