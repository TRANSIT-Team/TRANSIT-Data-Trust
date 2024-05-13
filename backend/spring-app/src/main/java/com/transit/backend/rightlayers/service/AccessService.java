package com.transit.backend.rightlayers.service;

import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.domain.AccessResponseList;
import com.transit.backend.security.filterresponse.helper.StorageRights;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AccessService {
	
	boolean updateConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId);
	
	public boolean updateConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId, UUID requestedById);
	
	Optional<AccessResponseDTO> getAccess(UUID oId);
	
	Optional<AccessResponseDTO> getAccess(UUID oId, UUID identityId);
	
	public Optional<AccessResponseDTO> getAccess(UUID oId, UUID identityId, UUID requestedById);
	
	
	StorageRights getAccessList(Set<UUID> objectIds);
	
	public StorageRights getAccessList(Set<UUID> objectIds, UUID identityId, UUID requestedById);
	
	
	AccessResponseList getAccessClazz(String entityClazz, boolean createdByMyCompany);
	
	AccessResponseList getAccessClazz(String entityClazz, boolean createdByMyCompany, UUID compID);
	
	
	AccessResponseList getAccessClazz(String entityClazz, UUID requestedById, boolean createdByMyCompany);
	
	boolean createConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId);
	
	public boolean createConnection(Set<String> readProperties, Set<String> writeProperties, UUID oId, UUID identityId, UUID requestedById);
	
	public boolean deleteConnectionRecursive(UUID oId, UUID identityId, UUID requestedById);
}
