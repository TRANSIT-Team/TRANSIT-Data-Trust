package com.transit.backend.datalayers.service;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PackageItemPackagePackagePropertyService extends CrudServiceSubRessource<PackagePackageProperty, UUID, UUID> {
	
	PackagePackageProperty create(UUID packageItemId, PackagePackageProperty entity);
	
	PackagePackageProperty update(UUID packageItemId, UUID packagePackagePropertiesId, PackagePackageProperty entity);
	
	PackagePackageProperty partialUpdate(UUID packageItemId, UUID packagePackagePropertiesId, JsonMergePatch patch);
	
	Collection<PackagePackageProperty> read(UUID packageItemId, String query, FilterExtra FilterExtra);
	
	Optional<PackagePackageProperty> readOne(UUID packageItemId, UUID packagePackagePropertiesId);
	
	
	void delete(UUID packageItemId, UUID packagePackagePropertiesId);
	
	
}