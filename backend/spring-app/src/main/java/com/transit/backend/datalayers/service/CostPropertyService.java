package com.transit.backend.datalayers.service;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CostPropertyService extends CrudServiceSubRessource<CostProperty, UUID, UUID> {
	
	CostProperty create(UUID costId, CostProperty entity);
	
	CostProperty update(UUID costId, UUID costPropertiesId, CostProperty entity);
	
	CostProperty partialUpdate(UUID costId, UUID costPropertiesId, JsonMergePatch patch);
	
	Collection<CostProperty> read(UUID costId, String query, FilterExtra FilterExtra);
	
	Optional<CostProperty> readOne(UUID costId, UUID costPropertiesId);
	
	
	void delete(UUID costId, UUID costPropertiesId);
	
	
}