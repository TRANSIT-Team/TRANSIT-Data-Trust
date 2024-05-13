package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.domain.QPackageProperty;
import com.transit.backend.datalayers.repository.PackagePropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PackagePropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePropertyMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.StreamSupport;


@Service
public class PackagePropertyServiceBean extends CrudServiceExtendAbstract<PackageProperty, PackagePropertyDTO> implements PackagePropertyService {
	@Inject
	Validator validator;
	@Autowired
	private PackagePropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PackagePropertyMapper mapper;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private UserHelperFunctions userHelperFunctions;

	@Override
	public PackageProperty create(PackageProperty entity) {
		var ownProperties = getOwnCreatedPackageProperties();
		if (!ownProperties.isEmpty()) {
			ownProperties.forEach(prop -> {
				if (prop.getKey().equals(entity.getKey())) {
					throw new UnprocessableEntityExeption("PackageProperty with key: " + entity.getKey() + " already exists.");
				}
			});
		}
		return super.saveInternal(super.createInternal(entity));
	}
	
	public List<PackageProperty> getOwnCreatedPackageProperties() {
		List<PackageProperty> packageProperties = new ArrayList<>();
		var rightsForEntity = rightsService.getAccessClazz(PackageProperty.class.getSimpleName(), true);
		if (rightsForEntity.getObjects().isEmpty()) {
			return packageProperties;
		}
		var values = new StringBuilder();
		values.append("id=in=(");
		Set<UUID> nodeIds = new HashSet<>();
		for (var right : rightsForEntity.getObjects()) {
			nodeIds.add(right.getObjectId());
		}
		for (var node : nodeIds) {
			values.append(node);
			values.append(",");
		}
		
		if (values.toString().endsWith(",")) {
			values.deleteCharAt(values.length() - 1);
		}
		values.append(")");
		
		var spec = RSQLQueryDslSupport.toPredicate(values.toString(), getQClazz());
		return StreamSupport.stream(getRepository().findAll(spec).spliterator(), false).toList();
	}
	
	@Override
	public AbstractRepository<PackageProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<PackageProperty, PackagePropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<PackageProperty> getEntityClazz() {
		return PackageProperty.class;
	}
	
	@Override
	public Class<PackagePropertyDTO> getEntityDTOClazz() {
		return PackagePropertyDTO.class;
	}
	
	@Override
	public Path<PackageProperty> getQClazz() {
		return QPackageProperty.packageProperty;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public PackageProperty update(UUID primaryKey, PackageProperty entity) {
		var ownProperties = getOwnCreatedPackageProperties();
		if (!ownProperties.isEmpty()) {
			ownProperties.forEach(prop -> {
				if (prop.getKey().equals(entity.getKey()) &&
						!prop.getId().equals(entity.getId())) {
					throw new UnprocessableEntityExeption("PackageProperty with key: " + entity.getKey() + " already exists.");
				}
			});
		}
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	@Override
	public PackageProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		var ownProperties = getOwnCreatedPackageProperties();
		if (!ownProperties.isEmpty()) {
			ownProperties.forEach(prop -> {
				Optional<PackageProperty> test = getRepository().findByIdAndDeleted(primaryKey, false);
				if (test.isEmpty()) {
					throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
				} else {
					try {
						PackagePropertyDTO testDTO = getMapper().toDto(test.get());
						JsonNode original = objectMapper.valueToTree(testDTO);
						JsonNode patched = patch.apply(original);
						var response = objectMapper.treeToValue(patched, getEntityDTOClazz());
						var entity = getMapper().toEntity(response);
						
						
						if (prop.getKey().equals(entity.getKey()) &&
								!prop.getId().equals(entity.getId())) {
							throw new UnprocessableEntityExeption("PackageProperty with key: " + entity.getKey() + " already exists.");
							
						}
					} catch (JsonPatchException | JsonProcessingException e) {
						throw new BadRequestException(e.getMessage());
					}
				}
			});
		}
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
		
		
	}
	
	@Override
	public Page<PackageProperty> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<PackageProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
}