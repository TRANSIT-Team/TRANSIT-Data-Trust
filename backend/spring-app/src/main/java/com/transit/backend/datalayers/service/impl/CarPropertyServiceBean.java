package com.transit.backend.datalayers.service.impl;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.QCarProperty;
import com.transit.backend.datalayers.repository.CarPropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CarPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceNestedAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CarPropertyServiceBean extends CrudServiceNestedAbstract<CarProperty, UUID, CarPropertyDTO, Car> implements CarPropertyService {
	
	@Autowired
	private CarPropertyRepository carPropertyRepository;
	
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	
	@Override
	public AbstractRepository<CarProperty> getRepository() {
		return this.carPropertyRepository;
	}
	
	@Override
	public Class<CarProperty> getClazz() {
		return CarProperty.class;
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getMapper() {
		return this.carPropertyMapper;
	}
	
	@Override
	public Class<CarPropertyDTO> getDTOClazz() {
		return CarPropertyDTO.class;
	}
	
	@Override
	public Path getQClazz() {
		return QCarProperty.carProperty;
	}
	
	@Override
	public CarProperty update(UUID primaryKey, CarProperty entity) {
		return super.updateInternal(primaryKey, entity);
	}
	
	
	@Override
	public CarProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.partialUpdateInternal(primaryKey, patch);
		
	}
	
	@Override
	public Optional<CarProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.deleteInternal(primaryKey);
	}
	
}