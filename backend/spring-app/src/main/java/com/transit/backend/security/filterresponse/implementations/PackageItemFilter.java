package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackageItemFilter extends AbstractParentEntityFilter<PackageItem, PackageItem, PackagePackageProperty, PackagePackageProperty> implements EntityFilterHelper<PackageItem, PackageItem> {
	
	
	@Autowired
	private PackagePackagePropertyFilter packagePackagePropertyFilter;
	
	@Override
	public PackageItem transformToTransfer(PackageItem entity) {
		return entity;
	}
	
	@Override
	public PackageItem transformToEntity(PackageItem entity) {
		return entity;
	}
	
	@Override
	public PackageItem transformToTransfer(PackageItem entity, PackageItem entityOld) {
		return entity;
	}
	
	@Override
	public Class<PackageItem> getClazz() {
		return PackageItem.class;
	}
	
	@Override
	public String getPathToEntity(PackageItem entity, PackageItem entity2) {
		return "/packageitems/" + entity.getId();
	}
	
	@Override
	public PackageItem filterEntities(PackageItem entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		//PackageClass können alle lesen, somit kein Filter
		return entity;
	}
	
	@Override
	public AbstractPropertyEntityFilter<PackagePackageProperty, PackagePackageProperty, PackageItem, PackageItem> getPropertyFilter() {
		return this.packagePackagePropertyFilter;
	}
}
