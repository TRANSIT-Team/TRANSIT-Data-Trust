package com.transit.backend.rightlayers.service;

import com.transit.backend.rightlayers.domain.UserAttributeClazz;

import java.util.Optional;
import java.util.UUID;

public interface IdentityService {
	
	public UserAttributeClazz createIdentity(UUID identityId);
	
	public Optional<UserAttributeClazz> getIdentity(UUID identityId);
	
	public boolean deleteIdentity(UUID id);
	
	
}
