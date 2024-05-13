package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.QWarehouseProperty;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.repository.WarehousePropertyRepository;
import com.transit.backend.datalayers.repository.WarehouseRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.WarehouseWarehousePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseWarehousePropertyServiceBean extends CrudServiceSubRessourceAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse> implements WarehouseWarehousePropertyService {
	@Autowired
	private WarehousePropertyRepository warehousePropertyRepository;
	
	@Autowired
	private WarehouseRepository warehouseRepository;
	
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	
	@Override
	public AbstractRepository<WarehouseProperty> getPropertyRepository() {
		return this.warehousePropertyRepository;
	}
	
	@Override
	public AbstractRepository<Warehouse> getParentRepository() {
		return this.warehouseRepository;
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getPropertyMapper() {
		return this.warehousePropertyMapper;
	}
	
	@Override
	public Class<WarehouseProperty> getPropertyClazz() {
		return WarehouseProperty.class;
	}
	
	@Override
	public Class<WarehousePropertyDTO> getPropertyDTOClazz() {
		return WarehousePropertyDTO.class;
	}
	
	@Override
	public Class<Warehouse> getParentClass() {
		return Warehouse.class;
	}
	
	@Override
	public Path<WarehouseProperty> getPropertyQClazz() {
		return QWarehouseProperty.warehouseProperty;
	}
	
	@Override
	public String getParentString() {
		return "warehouse";
	}
	
	
	@Override
	public WarehouseProperty create(UUID warehouseId, WarehouseProperty entity) {
		return super.createInternal(warehouseId, entity);
	}
	
	@Override
	public WarehouseProperty update(UUID warehouseId, UUID warehousePropertyId, WarehouseProperty entity) {
		return super.updateInternal(warehouseId, warehousePropertyId, entity);
	}
	
	@Override
	public WarehouseProperty partialUpdate(UUID warehouseId, UUID warehousePropertyId, JsonMergePatch patch) {
		return super.partialUpdateInternal(warehouseId, warehousePropertyId, patch);
	}
	
	@Override
	public Collection<WarehouseProperty> read(UUID warehouseId, String query, FilterExtra FilterExtra) {
		return super.readInternal(warehouseId, query, FilterExtra);
	}
	
	@Override
	public Optional<WarehouseProperty> readOne(UUID warehouseId, UUID warehousePropertyId) {
		
		return super.readOneInternal(warehouseId, warehousePropertyId);
		
	}
	
	
	@Override
	public void delete(UUID warehouseId, UUID warehousePropertyId) {
		super.deleteInternal(warehouseId, warehousePropertyId);
	}
}
	


