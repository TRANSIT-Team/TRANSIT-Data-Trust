package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CostDTO;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.domain.QCost;
import com.transit.backend.datalayers.repository.CostPropertyRepository;
import com.transit.backend.datalayers.repository.CostRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CostPropertyService;
import com.transit.backend.datalayers.service.CostService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostMapper;
import com.transit.backend.datalayers.service.mapper.CostPropertyMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

@Service
public class CostServiceBean extends CrudServiceExtendPropertyAbstract<Cost, CostDTO, CostProperty, CostPropertyDTO> implements CostService {
	
	@Autowired
	private CostPropertyService costPropertyService;
	@Autowired
	private CostPropertyRepository costPropertyRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private CostPropertyMapper costPropertyMapper;
	@Autowired
	private CostMapper costMapper;
	
	@Override
	public Class<Cost> getEntityClazz() {
		return Cost.class;
	}
	
	@Override
	public Class<CostDTO> getEntityDTOClazz() {
		return CostDTO.class;
	}
	
	@Override
	public Path<Cost> getQClazz() {
		return QCost.cost;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public CrudServiceSubRessource<CostProperty, UUID, UUID> getPropertySubService() {
		return costPropertyService;
	}
	
	@Override
	public AbstractRepository<CostProperty> getPropertyRepository() {
		return costPropertyRepository;
	}
	
	@Override
	public AbstractMapper<CostProperty, CostPropertyDTO> getPropertyMapper() {
		return costPropertyMapper;
	}
	
	@Override
	public AbstractRepository<Cost> getRepository() {
		return costRepository;
	}
	
	@Override
	public AbstractMapper<Cost, CostDTO> getMapper() {
		return costMapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "costProperties.deleted==false";
		
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteCostToCostProperties(m);
	}
	
	@Override
	public Cost create(Cost entity) {
		entity = super.createInternal(entity);
		return super.saveInternal(entity);
	}
	
	@Override
	public Cost update(UUID primaryKey, Cost entity) {
		var entityOld = costRepository.findById(primaryKey);
		if (entityOld.isEmpty()) {
			throw new NoSuchElementFoundException(Cost.class.getSimpleName(), primaryKey);
		}
		entity = super.updateInternal(primaryKey, entity);
		return super.filterPUTPATCHInternal(super.saveInternal(entity));
		
	}
	
	@Override
	public Cost partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var oldSubOrder = costRepository.findById(primaryKey);
		if (oldSubOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Cost.class.getSimpleName(), primaryKey);
		}
		try {
			return super.filterPUTPATCHInternal(partialUpdateIntern(primaryKey, patch));
			
		} catch (Exception e) {
			throw new UnprocessableEntityExeption(e.getMessage());
		}
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Cost partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		var entityOld = costRepository.findById(primaryKey);
		var orderDTOPatched = super.partialUpdateInternal(primaryKey, patch);
		var patchedOrder = super.checkviolationsInternal(primaryKey, super.partialUpdateSavePropertiesInternal(orderDTOPatched));
		return super.saveInternal(patchedOrder);
	}
	
	@Override
	public Page<Cost> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Cost> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
}
