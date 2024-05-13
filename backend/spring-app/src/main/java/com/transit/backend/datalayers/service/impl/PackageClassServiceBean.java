package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.QPackageClass;
import com.transit.backend.datalayers.repository.PackageClassRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PackageClassService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackageClassMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class PackageClassServiceBean extends CrudServiceExtendAbstract<PackageClass, PackageClassDTO> implements PackageClassService {
	
	@Inject
	Validator validator;
	@Autowired
	private PackageClassRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PackageClassMapper mapper;

	@Override
	public PackageClass create(PackageClass entity) {
		if (repository.findPackageClassByName(entity.getName()).isPresent()) {
			throw new UnprocessableEntityExeption("PackageCLass with name: " + entity.getName() + " already exists.");
		}
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public PackageClass update(UUID primaryKey, PackageClass entity) {
		if (repository.findPackageClassByName(entity.getName()).isPresent() &
				!repository.findPackageClassByName(entity.getName()).get().getId().equals(entity.getId())) {
			throw new UnprocessableEntityExeption("PackageClass with name: " + entity.getName() + " already exists.");
		}
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	@Override
	public PackageClass partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		Optional<PackageClass> test = getRepository().findByIdAndDeleted(primaryKey, false);
		if (test.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		} else {
			try {
				PackageClassDTO testDTO = getMapper().toDto(test.get());
				JsonNode original = objectMapper.valueToTree(testDTO);
				JsonNode patched = patch.apply(original);
				var response = objectMapper.treeToValue(patched, getEntityDTOClazz());
				var entity = getMapper().toEntity(response);
				
				
				if (repository.findPackageClassByName(entity.getName()).isPresent() &&
						!repository.findPackageClassByName(entity.getName()).get().getId().equals(entity.getId())) {
					throw new UnprocessableEntityExeption("PackageClass with name: " + entity.getName() + " already exists.");
				}
			} catch (JsonPatchException | JsonProcessingException e) {
				throw new BadRequestException(e.getMessage());
			}
		}
		
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
	}
	
	@Override
	public Page<PackageClass> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<PackageClass> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	@Override
	public AbstractRepository<PackageClass> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<PackageClass, PackageClassDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<PackageClass> getEntityClazz() {
		return PackageClass.class;
	}
	
	@Override
	public Class<PackageClassDTO> getEntityDTOClazz() {
		return PackageClassDTO.class;
	}
	
	@Override
	public Path<PackageClass> getQClazz() {
		return QPackageClass.packageClass;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return false;
	}
}