package com.transit.graphbased_v2.service;

import com.transit.graphbased_v2.domain.graph.nodes.UserAttributeClazz;
import com.transit.graphbased_v2.exceptions.NodeIdExistsException;
import com.transit.graphbased_v2.exceptions.NodeNotFoundException;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.Optional;
import java.util.UUID;

public interface IdentityService {

	
	public UserAttributeClazz createIdentity(UserAttributeClazz userAttributeClazz) throws NodeIdExistsException;
	
	public Optional<UserAttributeClazz> getIdentity(UUID id) ;
	
	public boolean deleteIdentity(UUID id) throws NodeNotFoundException;
}
