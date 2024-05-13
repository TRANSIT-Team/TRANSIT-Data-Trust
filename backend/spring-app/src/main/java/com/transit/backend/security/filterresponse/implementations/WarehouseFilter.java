package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WarehouseFilter extends AbstractParentEntityFilter<Warehouse, Warehouse, WarehouseProperty, WarehouseProperty> implements EntityFilterHelper<Warehouse, Warehouse> {
	@Autowired
	private WarehousePropertyFilter warehousePropertyFilter;
	
	@Autowired
	private AddressFilter addressFilter;
	
	@Autowired
	private OrderLegFilter orderLegFilter;
	
	@Override
	public Warehouse transformToTransfer(Warehouse entity) {
		return entity;
	}
	
	@Override
	public Warehouse transformToEntity(Warehouse entity) {
		return entity;
	}
	
	@Override
	public Warehouse transformToTransfer(Warehouse entity, Warehouse entityOld) {
		return entity;
	}
	
	@Override
	public Class<Warehouse> getClazz() {
		return Warehouse.class;
	}
	
	@Override
	public String getPathToEntity(Warehouse entity, Warehouse entity2) {
		return "/warehouses/" + entity.getId();
	}
	
	@Override
	public Warehouse filterEntities(Warehouse entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		if (entity != null && entity.getAddress() != null) {
			entity.setAddress(addressFilter.filterEntities(entity.getAddress(), companyId, storageRights));
		}
		
		if (entity != null && entity.getOrderLegs() != null && !entity.getOrderLegs().isEmpty()) {
			List<UUID> orderLegsIds = new ArrayList<>();
			List<UUID> orderLegsIdsToRemove = new ArrayList<>();
			entity.getOrderLegs().forEach(prop -> orderLegsIds.add(prop.getId()));
			getUUIDsToDelete(orderLegsIds, orderLegsIdsToRemove, storageRights);
			var proper = entity
					.getOrderLegs()
					.stream()
					.filter(prop -> !orderLegsIdsToRemove.contains(prop.getId()))
					.map(prop -> orderLegFilter.filterEntities(prop, companyId, storageRights))
					.collect(Collectors.toSet());
			if (proper.isEmpty()) {
				entity.setOrderLegs(null);
			} else {
				SortedSet<OrderLeg> properSort = new TreeSet<>(proper);
				entity.setOrderLegs(properSort);
			}
		}
		
		return entity;
	}
	
	@Override
	public AbstractPropertyEntityFilter<WarehouseProperty, WarehouseProperty, Warehouse, Warehouse> getPropertyFilter() {
		return this.warehousePropertyFilter;
	}
	
	public Set<UUID> collectIDs(Warehouse entity) {
		var uuids = super.collectIDs(entity);
		if (entity != null && entity.getAddress() != null) {
			uuids.add(entity.getAddress().getId());
		}
		entity.getOrderLegs().forEach(prop -> uuids.add(prop.getId()));
		return uuids;
	}
	
	
}
