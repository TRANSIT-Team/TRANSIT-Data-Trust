package com.transit.backend.datalayers.service;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserUserPropertyService extends CrudServiceSubRessource<UserProperty, UUID, UUID> {
	UserProperty create(UUID userId, UserProperty entity);
	
	UserProperty update(UUID userId, UUID userPropertyId, UserProperty entity);
	
	UserProperty partialUpdate(UUID userId, UUID userPropertyId, JsonMergePatch patch);
	
	Collection<UserProperty> read(UUID userId, String query, FilterExtra FilterExtra);
	
	Optional<UserProperty> readOne(UUID userId, UUID userPropertyId);
	
	
	void delete(UUID userId, UUID userPropertyId);
}
