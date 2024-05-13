package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.service.CompanyUserService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class CompanyUserServiceBean implements CompanyUserService {
	@Override
	public User create(UUID companyId, User entity) {
		return null;
	}
	
	@Override
	public User update(UUID companyId, UUID userId, User entity) {
		return null;
	}
	
	@Override
	public User partialUpdate(UUID companyId, UUID userId, JsonMergePatch patch) {
		return null;
	}
	
	@Override
	public Collection<User> read(UUID companyId, String query) {
		return null;
	}
	
	@Override
	public Optional<User> readOne(UUID companyId, UUID userId) {
		return Optional.empty();
	}
	
	@Override
	public void delete(UUID companyId, UUID userId) {
	
	}
}
