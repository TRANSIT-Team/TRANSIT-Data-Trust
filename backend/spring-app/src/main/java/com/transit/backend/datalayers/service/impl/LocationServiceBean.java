package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.domain.QLocation;
import com.transit.backend.datalayers.repository.LocationRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.LocationService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class LocationServiceBean extends CrudServiceExtendAbstract<Location, LocationDTO> implements LocationService {
	
	@Inject
	Validator validator;
	@Autowired
	private LocationRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private LocationMapper mapper;

	@Override
	public AbstractRepository<Location> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<Location, LocationDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<Location> getEntityClazz() {
		return Location.class;
	}
	
	@Override
	public Class<LocationDTO> getEntityDTOClazz() {
		return LocationDTO.class;
	}
	
	@Override
	public Path<Location> getQClazz() {
		return QLocation.location;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public Location create(Location entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public Location update(UUID primaryKey, Location entity) {
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	
	@Override
	public Location partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
		
	}
	
	@Override
	public Page<Location> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Location> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	
}