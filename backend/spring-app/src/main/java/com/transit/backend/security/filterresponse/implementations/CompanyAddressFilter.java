package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyAddressFilter extends AbstractEntityFilter<CompanyAddress, Address> implements EntityFilterHelper<CompanyAddress, Address> {
	@Override
	public CompanyAddress transformToTransfer(Address entity) {
		throw new RuntimeException("Cannot transform Address without extra info to CompanyAddress");
	}
	
	@Override
	public CompanyAddress filterEntities(CompanyAddress entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public Address transformToEntity(CompanyAddress entity) {
		return entity.getAddress();
	}
	
	@Override
	public CompanyAddress transformToTransfer(Address entity, CompanyAddress entityOld) {
		entityOld.setAddress(entity);
		return entityOld;
	}
	
	@Override
	public Class<Address> getClazz() {
		return Address.class;
	}
	
	@Override
	public String getPathToEntity(CompanyAddress entity, Address entity2) {
		return "/companies/" + entity.getId().getCompanyId() + "/companyadresses/" + entity.getId().getAddressId();
	}
}
