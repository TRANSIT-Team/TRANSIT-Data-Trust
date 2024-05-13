package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.domain.QCostProperty;
import com.transit.backend.datalayers.repository.CostPropertyRepository;
import com.transit.backend.datalayers.repository.CostRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CostPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class CostPropertyServiceBean extends CrudServiceSubRessourceAbstract<CostProperty, CostPropertyDTO, Cost> implements CostPropertyService {
	@Autowired
	private CostPropertyRepository costPropertyRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private CostPropertyMapper costPropertyMapper;
	
	@Override
	public AbstractRepository<CostProperty> getPropertyRepository() {
		return costPropertyRepository;
	}
	
	@Override
	public AbstractRepository<Cost> getParentRepository() {
		return costRepository;
	}
	
	@Override
	public AbstractMapper<CostProperty, CostPropertyDTO> getPropertyMapper() {
		return costPropertyMapper;
	}
	
	@Override
	public Class<CostProperty> getPropertyClazz() {
		return CostProperty.class;
	}
	
	@Override
	public Class<CostPropertyDTO> getPropertyDTOClazz() {
		return CostPropertyDTO.class;
	}
	
	@Override
	public Class<Cost> getParentClass() {
		return Cost.class;
	}
	
	@Override
	public Path<CostProperty> getPropertyQClazz() {
		return QCostProperty.costProperty;
	}
	
	@Override
	public String getParentString() {
		return "cost";
	}
	
	@Override
	public CostProperty create(UUID uuid, CostProperty entity) {
		return super.createInternal(uuid, entity);
	}
	
	@Override
	public CostProperty update(UUID uuid, UUID uuid2, CostProperty entity) {
		return super.updateInternal(uuid, uuid2, entity);
	}
	
	@Override
	public CostProperty partialUpdate(UUID uuid, UUID uuid2, JsonMergePatch patch) {
		return super.partialUpdateInternal(uuid, uuid2, patch);
	}
	
	
	@Override
	public Collection<CostProperty> read(UUID uuid, String query, FilterExtra FilterExtra) {
		return super.readInternal(uuid, query, FilterExtra);
	}
	
	@Override
	public Optional<CostProperty> readOne(UUID uuid, UUID uuid2) {
		return super.readOneInternal(uuid, uuid2);
	}
	
	@Override
	public void delete(UUID uuid, UUID uuid2) {
		super.deleteInternal(uuid, uuid2);
	}
}
