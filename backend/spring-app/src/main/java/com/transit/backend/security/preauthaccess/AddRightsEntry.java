package com.transit.backend.security.preauthaccess;


import com.transit.backend.config.EndpointsByPath;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@Component
public class AddRightsEntry {
	
	
	@Autowired
	WebClient webClient;
	
	
	@Autowired
	private Repositories repositories;
	
	private UsersResource usersResource;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EndpointsByPath endpointsByPath;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	
	public AddRightsEntry(KeycloakServiceManager keycloakServiceManager) {
		this.usersResource = keycloakServiceManager.getUsersResource();
	}
	
	public boolean addEntry(UUID uuid) {
		var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
		
		var uriComponents = servletBuilder.build();
		var lastUriComponentBeforeId = uriComponents.getPathSegments().get(uriComponents.getPathSegments().size() - 2);
		String typeClazz = endpointsByPath.readOne(lastUriComponentBeforeId);
		if (typeClazz == null) {
			return false;
		}
		if (repositories.entityExists(uuid)) {
			Optional<AbstractEntity> entity = repositories.getEntity(uuid);
			if (entity.isPresent()) {
				var creatorMail = entity.get().getCreatedBy();
				
				var responseList = this.usersResource.search(creatorMail);
				if (responseList.isEmpty()) {
					return false;
				}
				var userRepresentation = responseList.get(0);
				var user = userRepository.getByKeycloakId(UUID.fromString(userRepresentation.getId()));
				rightsManageService.createEntityAndConnectIt(uuid, typeClazz, endpointsByPath.getClasses().get(typeClazz));
				//generateEntryRight.generateEntry(uuid, typeClazz.getBody(), user.getId());
				return true;
				
			} else {
				return false;
			}
			
			
		} else {
			throw new NoSuchElementFoundException(typeClazz, uuid);
			//return false;
		}
		
		
	}
	
	
}
