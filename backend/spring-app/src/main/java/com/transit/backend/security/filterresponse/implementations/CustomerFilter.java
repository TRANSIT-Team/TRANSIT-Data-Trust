package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class CustomerFilter extends AbstractEntityFilter<Customer, Customer> implements EntityFilterHelper<Customer, Customer> {
	@Override
	public Customer transformToTransfer(Customer entity) {
		return entity;
	}
	
	@Override
	public Customer filterEntities(Customer entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public Set<UUID> collectIDs(Customer customer) {
		return super.collectIDs(customer);
	}
	
	@Override
	public Customer transformToEntity(Customer entity) {
		return entity;
	}
	
	@Override
	public Customer transformToTransfer(Customer entity, Customer entityOld) {
		return entity;
	}
	
	@Override
	public Class<Customer> getClazz() {
		return Customer.class;
	}
	
	@Override
	public String getPathToEntity(Customer entity, Customer entity2) {
		return "/companies/" + entity.getCompanyId() + "/customers/" + entity.getId();
	}
}
