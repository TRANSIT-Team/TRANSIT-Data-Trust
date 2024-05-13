package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CarPropertyFilter extends AbstractPropertyEntityFilter<CarProperty, CarProperty, Car, Car> implements EntityFilterHelper<CarProperty, CarProperty> {
	@Override
	public CarProperty transformToTransfer(CarProperty entity) {
		return entity;
	}
	
	@Override
	public CarProperty transformToEntity(CarProperty entity) {
		return entity;
	}
	
	@Override
	public CarProperty transformToTransfer(CarProperty entity, CarProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<CarProperty> getClazz() {
		return CarProperty.class;
	}
	
	@Override
	public String getPathToEntity(CarProperty entity, CarProperty entity2) {
		return "/cars/" + entity.getCar().getId() + "/carproperties/" + entity.getId();
	}
	
	@Override
	public CarProperty filterEntities(CarProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		return entity;
	}
	
	
}
