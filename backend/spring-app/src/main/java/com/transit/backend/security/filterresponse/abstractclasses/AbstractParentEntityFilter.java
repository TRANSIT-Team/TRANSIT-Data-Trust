package com.transit.backend.security.filterresponse.abstractclasses;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public abstract class AbstractParentEntityFilter<test, test2 extends AbstractParentEntity<property2>, property, property2 extends AbstractPropertyEntity<test2>> extends AbstractEntityFilter<test, test2> implements EntityFilterHelper<test, test2> {
	
	@Override
	public test filterEntities(test entity, UUID companyId, StorageRights storageRights) {
		
		entity = super.filterEntities(entity, companyId, storageRights);
		if (entity != null) {
			var entityTemp = transformToEntity(entity);
			if (entityTemp != null && entityTemp.getProperties() != null && !entityTemp.getProperties().isEmpty()) {
				List<UUID> propertiesIds = new ArrayList<>();
				List<UUID> propertiesIdsToRemove = new ArrayList<>();
				
				
				entityTemp.getProperties().forEach(prop -> propertiesIds.add(prop.getId()));
				getUUIDsToDelete(propertiesIds, propertiesIdsToRemove, storageRights);
				var proper = entityTemp
						.getProperties()
						.stream()
						.filter(prop -> !propertiesIdsToRemove.contains(prop.getId()))
						.map(prop -> getPropertyFilter().filterEntities(getPropertyFilter().transformToTransfer(prop), companyId, storageRights))
						.map(prop -> getPropertyFilter().transformToEntity(prop))
						.collect(Collectors.toSet());
				if (proper.isEmpty()) {
					entityTemp.setProperties(null);
				} else {
					SortedSet<property2> properSort = new TreeSet<>(proper);
					entityTemp.setProperties(properSort);
				}
			}
			entity = transformToTransfer(entityTemp, entity);
		}
		return entity;
	}
	
	public abstract AbstractPropertyEntityFilter<property, property2, test, test2> getPropertyFilter();
	
	@Override
	public Set<UUID> collectIDs(test entityTransfer) {
		var entity = this.transformToEntity(entityTransfer);
		var listUUID = super.collectIDs(entityTransfer);
		entity.getProperties().forEach(p -> listUUID.add(p.getId()));
		return listUUID;
	}
}
