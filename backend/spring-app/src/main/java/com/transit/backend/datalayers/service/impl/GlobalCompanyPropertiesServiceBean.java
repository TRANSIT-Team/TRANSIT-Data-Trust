package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import com.transit.backend.datalayers.domain.QGlobalCompanyProperties;
import com.transit.backend.datalayers.repository.GlobalCompanyPropertiesRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.GlobalCompanyPropertiesService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.GlobalCompanyPropertiesMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GlobalCompanyPropertiesServiceBean extends CrudServiceExtendAbstract<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> implements GlobalCompanyPropertiesService {
	
	@Autowired
	private GlobalCompanyPropertiesRepository globalCompanyPropertiesRepository;
	@Autowired
	private GlobalCompanyPropertiesMapper mapper;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public GlobalCompanyProperties create(GlobalCompanyProperties entity) {
		if (globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getName()).isPresent()) {
			throw new UnprocessableEntityExeption("GlobalProperty with name: " + entity.getName() + " already exists.");
		}
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public GlobalCompanyProperties update(UUID primaryKey, GlobalCompanyProperties entity) {
		if (globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getName()).isPresent() &
				!globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getName()).get().getId().equals(entity.getId())) {
			throw new UnprocessableEntityExeption("GlobalProperty with name: " + entity.getName() + " already exists.");
		}
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	@Override
	public GlobalCompanyProperties partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Optional<GlobalCompanyProperties> test = getRepository().findByIdAndDeleted(primaryKey, false);
		if (test.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		} else {
			try {
				GlobalCompanyPropertiesDTO testDTO = getMapper().toDto(test.get());
				JsonNode original = objectMapper.valueToTree(testDTO);
				JsonNode patched = patch.apply(original);
				var response = objectMapper.treeToValue(patched, getEntityDTOClazz());
				var entity = getMapper().toEntity(response);
				
				
				if (globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getName()).isPresent() &&
						!globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getName()).get().getId().equals(entity.getId())) {
					throw new UnprocessableEntityExeption("GlobalProperty with name: " + entity.getName() + " already exists.");
				}
			} catch (JsonPatchException | JsonProcessingException e) {
				throw new BadRequestException(e.getMessage());
			}
		}
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
	}
	
	@Override
	public AbstractRepository<GlobalCompanyProperties> getRepository() {
		return this.globalCompanyPropertiesRepository;
	}
	
	@Override
	public AbstractMapper<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public Class<GlobalCompanyProperties> getEntityClazz() {
		return GlobalCompanyProperties.class;
	}
	
	@Override
	public Class<GlobalCompanyPropertiesDTO> getEntityDTOClazz() {
		return GlobalCompanyPropertiesDTO.class;
	}
	
	@Override
	public Path<GlobalCompanyProperties> getQClazz() {
		return QGlobalCompanyProperties.globalCompanyProperties;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return false;
	}
	
	@Override
	public Page<GlobalCompanyProperties> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<GlobalCompanyProperties> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
}