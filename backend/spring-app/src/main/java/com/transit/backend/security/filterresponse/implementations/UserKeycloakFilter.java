package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.transferentities.UserTransferObject;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserKeycloakFilter implements EntityFilterHelper<UserTransferObject, UserRepresentation> {
	
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public UUID filterEntitiesCompanyId() {
		return userHelperFunctions.getCompanyId();
	}
	
	@Override
	public UserTransferObject filterEntities(UserTransferObject entity, UUID companyId, StorageRights storageRights) {
		var tempEntity = entity.getUserRepresentation();
		
		Optional<AccessResponseDTO> rights;
		try {
			rights = rightsService.getAccess(entity.getUser().getId(), companyId);
		} catch (Exception e) {
			rights = Optional.empty();
		}
		
		if (rights.isEmpty()) {
			entity.setUserRepresentation(null);
			return entity;
		}
		entity.setUserRepresentation(tempEntity);
		return entity;
	}
	
	@Override
	public Set<UUID> collectIDs(UserTransferObject entityTransfer) {
		var entity = entityTransfer.getUserRepresentation();
		return new HashSet<>(Arrays.asList(repository.getByKeycloakId(UUID.fromString(entity.getId())).getId()));
	}
	
}
