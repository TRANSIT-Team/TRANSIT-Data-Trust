package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.domain.QPackagePackageProperty;
import com.transit.backend.datalayers.repository.PackageItemPackagePropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PackageItemPackagePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceNestedAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class PackageItemPackagePropertyServiceBean extends CrudServiceNestedAbstract<PackagePackageProperty, UUID, PackagePackagePropertyDTO, PackageItem> implements PackageItemPackagePropertyService {
	
	@Inject
	Validator validator;
	@Autowired
	private PackageItemPackagePropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PackagePackagePropertyMapper mapper;
	
	@Override
	public PackagePackageProperty update(UUID primaryKey, PackagePackageProperty entity) {
		return super.updateInternal(primaryKey, entity);
	}
	
	
	@Override
	public PackagePackageProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.partialUpdateInternal(primaryKey, patch);
		
	}
	
	@Override
	public Optional<PackagePackageProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.deleteInternal(primaryKey);
	}
	
	@Override
	public AbstractRepository<PackagePackageProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public Class<PackagePackageProperty> getClazz() {
		return PackagePackageProperty.class;
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<PackagePackagePropertyDTO> getDTOClazz() {
		return PackagePackagePropertyDTO.class;
	}
	
	@Override
	public Path getQClazz() {
		return QPackagePackageProperty.packagePackageProperty;
	}
}
