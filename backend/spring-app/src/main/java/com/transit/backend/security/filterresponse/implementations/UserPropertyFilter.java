package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPropertyFilter extends AbstractPropertyEntityFilter<UserProperty, UserProperty, UserTransferObject, User> implements EntityFilterHelper<UserProperty, UserProperty> {
	@Override
	public UserProperty transformToTransfer(UserProperty entity) {
		return entity;
	}
	
	@Override
	public UserProperty transformToEntity(UserProperty entity) {
		return entity;
	}
	
	@Override
	public UserProperty transformToTransfer(UserProperty entity, UserProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<UserProperty> getClazz() {
		return UserProperty.class;
	}
	
	@Override
	public String getPathToEntity(UserProperty entity, UserProperty entity2) {
		return "/users/" + entity.getUser().getId() + "/userproperties/" + entity.getId();
	}
	
	@Override
	public UserProperty filterEntities(UserProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
}
