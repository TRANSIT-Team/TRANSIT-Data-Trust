package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackagePropertyFilter extends AbstractEntityFilter<PackageProperty, PackageProperty> implements EntityFilterHelper<PackageProperty, PackageProperty> {
	
	@Override
	public PackageProperty transformToTransfer(PackageProperty entity) {
		return entity;
	}
	
	@Override
	public PackageProperty filterEntities(PackageProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		//Company wird nicht mit übertragen
		return entity;
	}
	
	@Override
	public PackageProperty transformToEntity(PackageProperty entity) {
		return entity;
	}
	
	@Override
	public PackageProperty transformToTransfer(PackageProperty entity, PackageProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<PackageProperty> getClazz() {
		return PackageProperty.class;
	}
	
	@Override
	public String getPathToEntity(PackageProperty entity, PackageProperty entity2) {
		return "/packageproperties/" + entity.getId();
	}
}
