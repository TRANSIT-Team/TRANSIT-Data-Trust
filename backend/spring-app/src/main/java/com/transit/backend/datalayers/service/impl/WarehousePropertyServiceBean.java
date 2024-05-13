package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.QWarehouseProperty;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.repository.WarehousePropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.WarehousePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceNestedAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WarehousePropertyServiceBean extends CrudServiceNestedAbstract<WarehouseProperty, UUID, WarehousePropertyDTO, Warehouse> implements WarehousePropertyService {
	
	@Autowired
	private WarehousePropertyRepository warehousePropertyRepository;
	
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	
	
	@Override
	public AbstractRepository<WarehouseProperty> getRepository() {
		return this.warehousePropertyRepository;
	}
	
	@Override
	public Class<WarehouseProperty> getClazz() {
		return WarehouseProperty.class;
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getMapper() {
		return this.warehousePropertyMapper;
	}
	
	@Override
	public Class<WarehousePropertyDTO> getDTOClazz() {
		return WarehousePropertyDTO.class;
	}
	
	@Override
	public Path getQClazz() {
		return QWarehouseProperty.warehouseProperty;
	}
	
	@Override
	public WarehouseProperty update(UUID primaryKey, WarehouseProperty entity) {
		return super.updateInternal(primaryKey, entity);
	}
	
	
	@Override
	public WarehouseProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.partialUpdateInternal(primaryKey, patch);
		
	}
	
	@Override
	public Optional<WarehouseProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.deleteInternal(primaryKey);
	}
	
}