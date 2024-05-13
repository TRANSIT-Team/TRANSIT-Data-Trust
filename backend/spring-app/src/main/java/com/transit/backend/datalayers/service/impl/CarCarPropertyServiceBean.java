package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.QCarProperty;
import com.transit.backend.datalayers.repository.CarPropertyRepository;
import com.transit.backend.datalayers.repository.CarRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CarCarPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarCarPropertyServiceBean extends CrudServiceSubRessourceAbstract<CarProperty, CarPropertyDTO, Car> implements CarCarPropertyService {
	@Autowired
	private CarPropertyRepository carPropertyRepository;
	
	@Autowired
	private CarRepository carRepository;
	
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	
	@Override
	public AbstractRepository<CarProperty> getPropertyRepository() {
		return this.carPropertyRepository;
	}
	
	@Override
	public AbstractRepository<Car> getParentRepository() {
		return this.carRepository;
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getPropertyMapper() {
		return this.carPropertyMapper;
	}
	
	@Override
	public Class<CarProperty> getPropertyClazz() {
		return CarProperty.class;
	}
	
	@Override
	public Class<CarPropertyDTO> getPropertyDTOClazz() {
		return CarPropertyDTO.class;
	}
	
	@Override
	public Class<Car> getParentClass() {
		return Car.class;
	}
	
	@Override
	public Path<CarProperty> getPropertyQClazz() {
		return QCarProperty.carProperty;
	}
	
	@Override
	public String getParentString() {
		return "car";
	}
	
	@Override
	public CarProperty create(UUID carId, CarProperty entity) {
		return super.createInternal(carId, entity);
	}
	
	@Override
	public CarProperty update(UUID carId, UUID carPropertyId, CarProperty entity) {
		return super.updateInternal(carId, carPropertyId, entity);
	}
	
	@Override
	public CarProperty partialUpdate(UUID carId, UUID carPropertyId, JsonMergePatch patch) {
		return super.partialUpdateInternal(carId, carPropertyId, patch);
	}
	
	@Override
	public Collection<CarProperty> read(UUID carId, String query, FilterExtra collectionFilterExtra) {
		return super.readInternal(carId, query, collectionFilterExtra);
	}
	
	@Override
	public Optional<CarProperty> readOne(UUID carId, UUID carPropertyId) {
		
		return super.readOneInternal(carId, carPropertyId);
		
	}
	
	
	@Override
	public void delete(UUID carId, UUID carPropertyId) {
		super.deleteInternal(carId, carPropertyId);
	}
}
	


