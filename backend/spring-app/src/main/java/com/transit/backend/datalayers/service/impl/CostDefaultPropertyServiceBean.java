package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CostDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.CostDefaultProperty;
import com.transit.backend.datalayers.domain.QCostDefaultProperty;
import com.transit.backend.datalayers.repository.CostDefaultPropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CostDefaultPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostDefaultPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;

@Service
public class CostDefaultPropertyServiceBean extends CrudServiceExtendAbstract<CostDefaultProperty, CostDefaultPropertyDTO> implements CostDefaultPropertyService {
	
	@Inject
	Validator validator;
	@Autowired
	private CostDefaultPropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CostDefaultPropertyMapper mapper;

	@Override
	public CostDefaultProperty create(CostDefaultProperty entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public CostDefaultProperty update(UUID primaryKey, CostDefaultProperty entity) {
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	@Override
	public CostDefaultProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
		
		
	}
	
	@Override
	public Page<CostDefaultProperty> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<CostDefaultProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	@Override
	public AbstractRepository<CostDefaultProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<CostDefaultProperty, CostDefaultPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<CostDefaultProperty> getEntityClazz() {
		return CostDefaultProperty.class;
	}
	
	@Override
	public Class<CostDefaultPropertyDTO> getEntityDTOClazz() {
		return CostDefaultPropertyDTO.class;
	}
	
	@Override
	public Path<CostDefaultProperty> getQClazz() {
		return QCostDefaultProperty.costDefaultProperty;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	
}