package com.transit.backend.rightlayers.service;

import com.transit.backend.rightlayers.domain.ObjectResponseDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ObjectService {
	
	
	public ObjectResponseDTO createObject(UUID objectId, String objectEntityClass, UUID identityId, Set<String> properties);
	
	
	public Optional<ObjectResponseDTO> updateObject(UUID objectId, String objectEntityClass, UUID identityId, Set<String> properties);
	
	public Optional<ObjectResponseDTO> getObject(UUID objectId);
	
	
	boolean deleteObject(UUID identityId, UUID objectId);
}
