package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.domain.QPackagePackageProperty;
import com.transit.backend.datalayers.repository.PackageItemPackagePropertyRepository;
import com.transit.backend.datalayers.repository.PackageItemRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PackageItemPackagePackagePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;


@Service
public class PackageItemPackagePackagePropertyServiceBean extends CrudServiceSubRessourceAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem> implements PackageItemPackagePackagePropertyService {
	
	@Inject
	Validator validator;
	@Autowired
	private PackageItemPackagePropertyRepository packageItemPackagePropertyRepository;
	@Autowired
	private PackageItemRepository packageItemRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PackagePackagePropertyMapper mapper;
	
	@Override
	public PackagePackageProperty create(UUID packageItemId, PackagePackageProperty entity) {
		
		
		return super.createInternal(packageItemId, entity);
	}
	
	@Override
	public PackagePackageProperty update(UUID packageItemId, UUID packageItemPackagePropertyId, PackagePackageProperty entity) {
		return super.updateInternal(packageItemId, packageItemPackagePropertyId, entity);
	}
	
	@Override
	public PackagePackageProperty partialUpdate(UUID packageItemId, UUID packagePackagePropertiesId, JsonMergePatch patch) {
		return super.partialUpdateInternal(packageItemId, packagePackagePropertiesId, patch);
	}
	
	
	@Override
	public Collection<PackagePackageProperty> read(UUID packageItemId, String query, FilterExtra FilterExtra) {
		return super.readInternal(packageItemId, query, FilterExtra);
	}
	
	@Override
	public Optional<PackagePackageProperty> readOne(UUID packageItemId, UUID packagePackagePropertiesId) {
		
		return super.readOneInternal(packageItemId, packagePackagePropertiesId);
		
	}
	
	@Override
	public void delete(UUID packageItemId, UUID packagePackagePropertiesId) {
		super.deleteInternal(packageItemId, packagePackagePropertiesId);
	}
	
	
	@Override
	public AbstractRepository<PackagePackageProperty> getPropertyRepository() {
		return this.packageItemPackagePropertyRepository;
	}
	
	@Override
	public AbstractRepository<PackageItem> getParentRepository() {
		return this.packageItemRepository;
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getPropertyMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<PackagePackageProperty> getPropertyClazz() {
		return PackagePackageProperty.class;
	}
	
	@Override
	public Class<PackagePackagePropertyDTO> getPropertyDTOClazz() {
		return PackagePackagePropertyDTO.class;
	}
	
	@Override
	public Class<PackageItem> getParentClass() {
		return PackageItem.class;
	}
	
	@Override
	public Path<PackagePackageProperty> getPropertyQClazz() {
		return QPackagePackageProperty.packagePackageProperty;
	}
	
	@Override
	public String getParentString() {
		return "packageItem";
	}
}