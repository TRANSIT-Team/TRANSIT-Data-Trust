package com.transit.backend.datalayers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CompanyCustomerService extends CrudServiceSubRessource<Customer, UUID, UUID> {
	Customer create(UUID companyId, Customer entity);
	
	Customer update(UUID companyId, UUID customerId, Customer entity);
	
	Customer partialUpdate(UUID companyId, UUID customerId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	Collection<Customer> read(UUID companyId, String query, FilterExtra FilterExtra);
	
	Optional<Customer> readOne(UUID companyId, UUID customerId);
	
	
	void delete(UUID companyId, UUID customerId);
	
}
