package com.transit.backend.security.filterresponse.interfaces;


import com.transit.backend.security.filterresponse.helper.StorageRights;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;


@Component
public interface EntityFilterHelper<transferclazz, enityclazz> {
	
	public UUID filterEntitiesCompanyId();
	
	public transferclazz filterEntities(transferclazz entity, UUID companyId, StorageRights storageRights);
	
	public Set<UUID> collectIDs(transferclazz entity);
}
	

