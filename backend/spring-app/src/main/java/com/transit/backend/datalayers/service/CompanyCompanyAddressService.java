package com.transit.backend.datalayers.service;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;


public interface CompanyCompanyAddressService extends CrudServiceSubRessource<CompanyAddress, UUID, UUID> {
	CompanyAddress create(UUID companyId, CompanyAddress entity);
	
	CompanyAddress update(UUID companyId, UUID addressId, CompanyAddress entity);
	
	CompanyAddress partialUpdate(UUID companyId, UUID addressId, JsonMergePatch patch);
	
	Collection<CompanyAddress> read(UUID companyId, String query, FilterExtra collectionFilterExtra);
	
	Optional<CompanyAddress> readOne(UUID companyId, UUID addressId);
	
	
	void delete(UUID companyId, UUID addressId);
	
}