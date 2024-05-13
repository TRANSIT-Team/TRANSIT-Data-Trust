package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.QUserProperty;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.repository.UserPropertyRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.UserUserPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.UserPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserUserPropertyServiceBean extends CrudServiceSubRessourceAbstract<UserProperty, UserPropertyDTO, User> implements UserUserPropertyService {
	
	@Inject
	Validator validator;
	@Autowired
	private UserPropertyRepository userPropertyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserPropertyMapper mapper;
	
	@Override
	public UserProperty create(UUID useridId, UserProperty entity) {
		
		
		return super.createInternal(useridId, entity);
	}
	
	@Override
	public UserProperty update(UUID useridId, UUID userPropertyIdId, UserProperty entity) {
		return super.updateInternal(useridId, userPropertyIdId, entity);
	}
	
	@Override
	public UserProperty partialUpdate(UUID useridId, UUID userPropertyId, JsonMergePatch patch) {
		return super.partialUpdateInternal(useridId, userPropertyId, patch);
	}
	
	@Override
	public Collection<UserProperty> read(UUID useridId, String query, FilterExtra FilterExtra) {
		return super.readInternal(useridId, query, FilterExtra);
	}
	
	@Override
	public Optional<UserProperty> readOne(UUID useridId, UUID userPropertyId) {
		
		return super.readOneInternal(useridId, userPropertyId);
		
	}
	
	@Override
	public void delete(UUID useridId, UUID userPropertyId) {
		super.deleteInternal(useridId, userPropertyId);
	}
	
	
	@Override
	public AbstractRepository<UserProperty> getPropertyRepository() {
		return this.userPropertyRepository;
	}
	
	@Override
	public AbstractRepository<User> getParentRepository() {
		return this.userRepository;
	}
	
	@Override
	public AbstractMapper<UserProperty, UserPropertyDTO> getPropertyMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<UserProperty> getPropertyClazz() {
		return UserProperty.class;
	}
	
	@Override
	public Class<UserPropertyDTO> getPropertyDTOClazz() {
		return UserPropertyDTO.class;
	}
	
	@Override
	public Class<User> getParentClass() {
		return User.class;
	}
	
	@Override
	public Path<UserProperty> getPropertyQClazz() {
		return QUserProperty.userProperty;
	}
	
	@Override
	public String getParentString() {
		return "user";
	}
}

