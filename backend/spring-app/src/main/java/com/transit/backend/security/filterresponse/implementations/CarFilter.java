package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CarFilter extends AbstractParentEntityFilter<Car, Car, CarProperty, CarProperty> implements EntityFilterHelper<Car, Car> {
	@Autowired
	private CarPropertyFilter carPropertyFilter;
	
	
	@Autowired
	private LocationFilter locationFilter;
	
	@Autowired
	private OrderLegFilter orderLegFilter;
	
	@Override
	public Car transformToTransfer(Car entity) {
		return entity;
	}
	
	@Override
	public Car transformToEntity(Car entity) {
		return entity;
	}
	
	@Override
	public Car transformToTransfer(Car entity, Car entityOld) {
		return entity;
	}
	
	@Override
	public Class<Car> getClazz() {
		return Car.class;
	}
	
	@Override
	public String getPathToEntity(Car entity, Car entity2) {
		return "/cars/" + entity.getId();
	}
	
	@Override
	public Car filterEntities(Car entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		if (entity != null && entity.getLocations() != null && !entity.getLocations().isEmpty()) {
			List<UUID> locationsIds = new ArrayList<>();
			List<UUID> locationsIdsToRemove = new ArrayList<>();
			entity.getLocations().forEach(prop -> locationsIds.add(prop.getId()));
			getUUIDsToDelete(locationsIds, locationsIdsToRemove, storageRights);
			var proper = entity
					.getLocations()
					.stream()
					.filter(prop -> !locationsIdsToRemove.contains(prop.getId()))
					.map(prop -> locationFilter.filterEntities(prop, companyId, storageRights))
					.collect(Collectors.toSet());
			if (proper.isEmpty()) {
				entity.setProperties(null);
			} else {
				SortedSet<Location> properSort = new TreeSet<>(proper);
				entity.setLocations(properSort);
			}
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
	public AbstractPropertyEntityFilter<CarProperty, CarProperty, Car, Car> getPropertyFilter() {
		return this.carPropertyFilter;
	}
	
	@Override
	public Set<UUID> collectIDs(Car entityTransfer) {
		var entity = this.transformToEntity(entityTransfer);
		var uuids = super.collectIDs(entity);
		entity.getLocations().forEach(prop -> uuids.add(prop.getId()));
		entity.getOrderLegs().forEach(prop -> uuids.add(prop.getId()));
		return uuids;
	}
	
	
}
