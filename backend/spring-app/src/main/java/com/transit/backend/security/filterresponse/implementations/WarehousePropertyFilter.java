package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WarehousePropertyFilter extends AbstractPropertyEntityFilter<WarehouseProperty, WarehouseProperty, Warehouse, Warehouse> implements EntityFilterHelper<WarehouseProperty, WarehouseProperty> {
	@Override
	public WarehouseProperty transformToTransfer(WarehouseProperty entity) {
		return entity;
	}
	
	@Override
	public WarehouseProperty transformToEntity(WarehouseProperty entity) {
		return entity;
	}
	
	@Override
	public WarehouseProperty transformToTransfer(WarehouseProperty entity, WarehouseProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<WarehouseProperty> getClazz() {
		return WarehouseProperty.class;
	}
	
	@Override
	public String getPathToEntity(WarehouseProperty entity, WarehouseProperty entity2) {
		return "/warehouses/" + entity.getWarehouse().getId() + "/warehouseproperties/" + entity.getId();
	}
	
	@Override
	public WarehouseProperty filterEntities(WarehouseProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	
}