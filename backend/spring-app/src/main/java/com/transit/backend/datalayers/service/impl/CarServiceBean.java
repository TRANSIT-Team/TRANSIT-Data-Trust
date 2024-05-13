package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.QCar;
import com.transit.backend.datalayers.repository.CarPropertyRepository;
import com.transit.backend.datalayers.repository.CarRepository;
import com.transit.backend.datalayers.repository.LocationRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CarCarPropertyService;
import com.transit.backend.datalayers.service.CarService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
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
public class CarServiceBean extends CrudServiceExtendPropertyAbstract<Car, CarDTO, CarProperty, CarPropertyDTO> implements CarService {
	
	
	@Autowired
	private CarPropertyRepository carPropertyRepository;
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private CarMapper carMapper;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private CarCarPropertyService carCarPropertyService;
	
	@Override
	public CrudServiceSubRessource<CarProperty, UUID, UUID> getPropertySubService() {
		return this.carCarPropertyService;
	}
	
	@Override
	public AbstractRepository<CarProperty> getPropertyRepository() {
		return this.carPropertyRepository;
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getPropertyMapper() {
		return this.carPropertyMapper;
	}
	
	@Override
	public AbstractRepository<Car> getRepository() {
		return this.carRepository;
	}
	
	@Override
	public AbstractMapper<Car, CarDTO> getMapper() {
		return this.carMapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "carProperties.deleted==false";
		
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteCarToCarProperties(m);
	}
	
	@Override
	public Car create(Car entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public Car update(UUID primaryKey, Car entity) {
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			entity = super.updateInternal(primaryKey, entity);
			entity.setLocations(oldEntity.get().getLocations());
			entity.setOrderLegs(oldEntity.get().getOrderLegs());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	@Override
	public Class<Car> getEntityClazz() {
		return Car.class;
	}
	
	@Override
	public Class<CarDTO> getEntityDTOClazz() {
		return CarDTO.class;
	}
	
	@Override
	public Path<Car> getQClazz() {
		return QCar.car;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public Car partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var com = partialUpdateIntern(primaryKey, patch);
		
		return super.filterPUTPATCHInternal(com);
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Car partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			var entityDto = super.partialUpdateSavePropertiesInternal(super.partialUpdateInternal(primaryKey, patch));
			var entity = super.checkviolationsInternal(primaryKey, entityDto);
			entity.setLocations(oldEntity.get().getLocations());
			entity.setOrderLegs(oldEntity.get().getOrderLegs());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	
	@Override
	public Page<Car> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Car> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(deleteInternal(super.deleteInternal(primaryKey)));
	}
	
	private Car deleteInternal(Car car) {
		if (car.getLocations() != null) {
			car.getLocations().stream().peek(loc -> {
				loc.setDeleted(true);
				locationRepository.saveAndFlush(loc);
			});
		}
		
		return car;
	}
}
