package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.QWarehouse;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.WarehousePropertyRepository;
import com.transit.backend.datalayers.repository.WarehouseRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.WarehouseService;
import com.transit.backend.datalayers.service.WarehouseWarehousePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehouseMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
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
public class WarehouseServiceBean extends CrudServiceExtendPropertyAbstract<Warehouse, WarehouseDTO, WarehouseProperty, WarehousePropertyDTO> implements WarehouseService {
	
	@Autowired
	private WarehousePropertyRepository warehousePropertyRepository;
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private WarehouseMapper warehouseMapper;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private WarehouseWarehousePropertyService warehouseWarehousePropertyService;
	
	@Override
	public CrudServiceSubRessource<WarehouseProperty, UUID, UUID> getPropertySubService() {
		return this.warehouseWarehousePropertyService;
	}
	
	@Override
	public AbstractRepository<WarehouseProperty> getPropertyRepository() {
		return this.warehousePropertyRepository;
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getPropertyMapper() {
		return this.warehousePropertyMapper;
	}
	
	@Override
	public AbstractRepository<Warehouse> getRepository() {
		return this.warehouseRepository;
	}
	
	@Override
	public AbstractMapper<Warehouse, WarehouseDTO> getMapper() {
		return this.warehouseMapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "warehouseProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteWarehouseToWarehouseProperties(m);
	}
	
	@Override
	public Warehouse create(Warehouse entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public Warehouse update(UUID primaryKey, Warehouse entity) {
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			entity = super.updateInternal(primaryKey, entity);
			entity.setAddress(oldEntity.get().getAddress());
			entity.setOrderLegs(oldEntity.get().getOrderLegs());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	@Override
	public Class<Warehouse> getEntityClazz() {
		return Warehouse.class;
	}
	
	@Override
	public Class<WarehouseDTO> getEntityDTOClazz() {
		return WarehouseDTO.class;
	}
	
	@Override
	public Path<Warehouse> getQClazz() {
		return QWarehouse.warehouse;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public Warehouse partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var com = partialUpdateIntern(primaryKey, patch);
		
		return super.filterPUTPATCHInternal(com);
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Warehouse partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			var entityDto = super.partialUpdateSavePropertiesInternal(super.partialUpdateInternal(primaryKey, patch));
			var entity = super.checkviolationsInternal(primaryKey, entityDto);
			entity.setAddress(oldEntity.get().getAddress());
			entity.setOrderLegs(oldEntity.get().getOrderLegs());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	
	@Override
	public Page<Warehouse> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Warehouse> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(deleteInternal(super.deleteInternal(primaryKey)));
	}
	
	private Warehouse deleteInternal(Warehouse warehouse) {
		if (warehouse.getAddress() != null) {
			warehouse.getAddress().setDeleted(true);
			addressRepository.saveAndFlush(warehouse.getAddress());
		}
		
		return warehouse;
	}
}


