package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFilter extends AbstractParentEntityFilter<UserTransferObject, User, UserProperty, UserProperty> implements EntityFilterHelper<UserTransferObject, User> {
	@Autowired
	private UserKeycloakFilter userKeycloakFilter;
	
	@Autowired
	private UserPropertyFilter userPropertyFilter;
	
	@Override
	public UserTransferObject transformToTransfer(User entity) {
		throw new RuntimeException("Cannot transform User to UserTransferObject");
	}
	
	@Override
	public User transformToEntity(UserTransferObject entity) {
		return entity.getUser();
	}
	
	@Override
	public UserTransferObject transformToTransfer(User entity, UserTransferObject entityOld) {
		entityOld.setUser(entity);
		return entityOld;
	}
	
	@Override
	public Class<User> getClazz() {
		return User.class;
	}
	
	@Override
	public String getPathToEntity(UserTransferObject entity, User entity2) {
		return "/users/" + entity.getUser().getId();
	}
	
	@Override
	public UserTransferObject filterEntities(UserTransferObject entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		entity = userKeycloakFilter.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	@Override
	public AbstractPropertyEntityFilter<UserProperty, UserProperty, UserTransferObject, User> getPropertyFilter() {
		return this.userPropertyFilter;
	}
}
