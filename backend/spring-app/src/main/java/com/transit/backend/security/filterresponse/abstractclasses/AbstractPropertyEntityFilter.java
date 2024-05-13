package com.transit.backend.security.filterresponse.abstractclasses;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public abstract class AbstractPropertyEntityFilter<property, property2 extends AbstractPropertyEntity<parentEntity>, test2, parentEntity extends AbstractParentEntity<property2>> extends AbstractEntityFilter<property, property2> implements EntityFilterHelper<property, property2> {
	
	@Override
	public property filterEntities(property entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
}
