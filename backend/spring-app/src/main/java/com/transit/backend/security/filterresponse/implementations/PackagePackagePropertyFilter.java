package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackagePackagePropertyFilter extends AbstractPropertyEntityFilter<PackagePackageProperty, PackagePackageProperty, PackageItem, PackageItem> implements EntityFilterHelper<PackagePackageProperty, PackagePackageProperty> {
	@Override
	public PackagePackageProperty transformToTransfer(PackagePackageProperty entity) {
		return entity;
	}
	
	@Override
	public PackagePackageProperty transformToEntity(PackagePackageProperty entity) {
		return entity;
	}
	
	@Override
	public PackagePackageProperty transformToTransfer(PackagePackageProperty entity, PackagePackageProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<PackagePackageProperty> getClazz() {
		return PackagePackageProperty.class;
	}
	
	@Override
	public String getPathToEntity(PackagePackageProperty entity, PackagePackageProperty entity2) {
		return "/packageitems/" + entity.getPackageItem().getId() + "/packagepackageproperties/" + entity.getId();
	}
	
	@Override
	public PackagePackageProperty filterEntities(PackagePackageProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
}
