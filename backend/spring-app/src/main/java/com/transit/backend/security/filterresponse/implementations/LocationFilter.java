package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LocationFilter extends AbstractEntityFilter<Location, Location> implements EntityFilterHelper<Location, Location> {
	
	@Override
	public Location transformToTransfer(Location entity) {
		return entity;
	}
	
	@Override
	public Location filterEntities(Location entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public Location transformToEntity(Location entity) {
		return entity;
	}
	
	@Override
	public Location transformToTransfer(Location entity, Location entityOld) {
		return entity;
	}
	
	@Override
	public Class<Location> getClazz() {
		return Location.class;
	}
	
	@Override
	public String getPathToEntity(Location entity, Location entity2) {
		return "/locations/" + entity.getId();
	}
}
