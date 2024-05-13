package com.transit.backend.datalayers.service;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CompanyUserService {
	User create(UUID companyId, User entity);
	
	User update(UUID companyId, UUID userId, User entity);
	
	User partialUpdate(UUID companyId, UUID userId, JsonMergePatch patch);
	
	Collection<User> read(UUID companyId, String query);
	
	Optional<User> readOne(UUID companyId, UUID userId);
	
	
	void delete(UUID companyId, UUID userId);
	
}
