package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.QUserProperty;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.repository.UserPropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.UserPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceNestedAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.UserPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserPropertyServiceBean extends CrudServiceNestedAbstract<UserProperty, UUID, UserPropertyDTO, User> implements UserPropertyService {
	
	
	@Inject
	Validator validator;
	@Autowired
	private UserPropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserPropertyMapper mapper;
	
	@Override
	public UserProperty update(UUID primaryKey, UserProperty entity) {
		return super.updateInternal(primaryKey, entity);
	}
	
	
	@Override
	public UserProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.partialUpdateInternal(primaryKey, patch);
		
	}
	
	@Override
	public Optional<UserProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.deleteInternal(primaryKey);
	}
	
	@Override
	public AbstractRepository<UserProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public Class<UserProperty> getClazz() {
		return UserProperty.class;
	}
	
	@Override
	public AbstractMapper<UserProperty, UserPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<UserPropertyDTO> getDTOClazz() {
		return UserPropertyDTO.class;
	}
	
	@Override
	public Path getQClazz() {
		return QUserProperty.userProperty;
	}
}