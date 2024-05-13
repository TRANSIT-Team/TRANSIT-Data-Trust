package com.transit.backend.datalayers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CompanyCompanyPropertyService extends CrudServiceSubRessource<CompanyProperty, UUID, UUID> {
	CompanyProperty create(UUID companyId, CompanyProperty entity);
	
	CompanyProperty update(UUID companyId, UUID companyPropertyId, CompanyProperty entity);
	
	CompanyProperty partialUpdate(UUID companyId, UUID companyPropertyId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	Collection<CompanyProperty> read(UUID companyId, String query, FilterExtra collectionFilterExtra);
	
	Optional<CompanyProperty> readOne(UUID companyId, UUID companyPropertyId);
	
	
	void delete(UUID companyId, UUID companyPropertyId);
	
}
