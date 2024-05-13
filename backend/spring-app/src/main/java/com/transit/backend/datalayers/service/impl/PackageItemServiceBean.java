package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.domain.QPackageItem;
import com.transit.backend.datalayers.repository.PackageClassRepository;
import com.transit.backend.datalayers.repository.PackageItemPackagePropertyRepository;
import com.transit.backend.datalayers.repository.PackageItemRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PackageItemPackagePackagePropertyService;
import com.transit.backend.datalayers.service.PackageItemService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackageItemMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackageClassMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;


/**
 * PUT PATCH POST PRÜFEN OB PACAKGEPROPERTIES ID FÜR BESTIMMTE FIRMA
 */

@Service
public class PackageItemServiceBean extends CrudServiceExtendPropertyAbstract<PackageItem, PackageItemDTO, PackagePackageProperty, PackagePackagePropertyDTO> implements PackageItemService {
	
	private final int[] finalLongestConditionNameArray = {-1};
	@Inject
	Validator validator;
	@Autowired
	private PackageItemRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PackageItemMapper mapper;
	@Autowired
	private PackageClassRepository classRepository;
	@Autowired
	private PackagePackageClassMapper classMapper;
	@Autowired
	private PackageItemPackagePropertyRepository propertiesRepository;
	@Autowired
	private PackagePackagePropertyMapper packagePackagePropertyMapper;
	@Autowired
	private PackageItemPackagePackagePropertyService packageItemPackagePackagePropertyService;

	@Override
	public PackageItem create(PackageItem entity) {
		
		entity = super.createInternal(entity);
		if (entity.getPackageClass() != null) {
			entity.setPackageClass(findPackageClass(entity.getPackageClass()));
		}
		return super.saveInternal(entity);
	}
	
	@Override
	public PackageItem update(UUID primaryKey, PackageItem entity) {
		
		entity = super.updateInternal(primaryKey, entity);
		
		if (entity.getPackageClass() != null) {
			entity.setPackageClass(findPackageClass(entity.getPackageClass()));
		}
		
		return super.filterPUTPATCHInternal(super.saveInternal(entity));
		
	}
	
	@Override
	public PackageItem partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.filterPUTPATCHInternal(partialUpdateIntern(primaryKey, patch));
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public PackageItem partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		var packageItemDTOPatched = super.partialUpdateInternal(primaryKey, patch);
		if (packageItemDTOPatched.getPackageClass() != null) {
			packageItemDTOPatched.setPackageClass(classMapper.toDto(findPackageClass(classMapper.toEntity(packageItemDTOPatched.getPackageClass()))));
		}
		
		
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateSavePropertiesInternal(packageItemDTOPatched)));
	}
	
	@Override
	public Page<PackageItem> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
		
		
	}
	
	@Override
	public Optional<PackageItem> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	private PackageClass findPackageClass(PackageClass packageClassEntity) {
		return classRepository.findById(packageClassEntity.getId()).filter(packageClass -> !packageClass.isDeleted())
				.orElseThrow(() -> new NoSuchElementFoundException(PackageClass.class.getSimpleName(), packageClassEntity.getId()));
	}
	
	@Override
	public Class<PackageItem> getEntityClazz() {
		return PackageItem.class;
	}
	
	@Override
	public Class<PackageItemDTO> getEntityDTOClazz() {
		return PackageItemDTO.class;
	}
	
	@Override
	public Path<PackageItem> getQClazz() {
		return QPackageItem.packageItem;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public CrudServiceSubRessource<PackagePackageProperty, UUID, UUID> getPropertySubService() {
		return this.packageItemPackagePackagePropertyService;
	}
	
	@Override
	public AbstractRepository<PackagePackageProperty> getPropertyRepository() {
		return this.propertiesRepository;
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getPropertyMapper() {
		return this.packagePackagePropertyMapper;
	}
	
	@Override
	public AbstractRepository<PackageItem> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<PackageItem, PackageItemDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "packagePackageProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewritePackageItemToPackagePackageProperty(m);
	}
}

