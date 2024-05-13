package com.transit.backend.security.filterresponse.implementations;


import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class AddressFilter extends AbstractEntityFilter<Address, Address> implements EntityFilterHelper<Address, Address> {
	@Override
	public Address transformToTransfer(Address entity) {
		return entity;
	}
	
	@Override
	public Address filterEntities(Address entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public Set<UUID> collectIDs(Address address) {
		return super.collectIDs(address);
	}
	
	@Override
	public Address transformToEntity(Address entity) {
		return entity;
	}
	
	@Override
	public Address transformToTransfer(Address entity, Address entityOld) {
		return entity;
	}
	
	@Override
	public Class<Address> getClazz() {
		return Address.class;
	}
	
	@Override
	public String getPathToEntity(Address entity, Address entity2) {
		return "/addresses/" + entity.getId();
	}
}
