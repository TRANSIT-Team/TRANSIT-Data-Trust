package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackageClassFilter extends AbstractEntityFilter<PackageClass, PackageClass> implements EntityFilterHelper<PackageClass, PackageClass> {
	
	@Override
	public PackageClass transformToTransfer(PackageClass entity) {
		return entity;
	}
	
	@Override
	public PackageClass filterEntities(PackageClass entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public PackageClass transformToEntity(PackageClass entity) {
		return entity;
	}
	
	@Override
	public PackageClass transformToTransfer(PackageClass entity, PackageClass entityOld) {
		return entity;
	}
	
	@Override
	public Class<PackageClass> getClazz() {
		return PackageClass.class;
	}
	
	@Override
	public String getPathToEntity(PackageClass entity, PackageClass entity2) {
		return "/packageclasses/" + entity.getId();
	}
}
