package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.domain.QCompanyProperty;
import com.transit.backend.datalayers.repository.CompanyPropertyRepository;
import com.transit.backend.datalayers.repository.GlobalCompanyPropertiesRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CompanyPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceNestedAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class CompanyPropertyServiceBean extends CrudServiceNestedAbstract<CompanyProperty, UUID, CompanyPropertyDTO, Company> implements CompanyPropertyService {
	
	
	@Inject
	Validator validator;
	@Autowired
	private CompanyPropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CompanyPropertyMapper mapper;
	@Autowired
	private GlobalCompanyPropertiesRepository globalCompanyPropertiesRepository;
	
	@Override
	public AbstractRepository<CompanyProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public Class<CompanyProperty> getClazz() {
		return CompanyProperty.class;
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<CompanyPropertyDTO> getDTOClazz() {
		return CompanyPropertyDTO.class;
	}
	
	@Override
	public Path getQClazz() {
		return QCompanyProperty.companyProperty;
	}
	
	@Override
	public CompanyProperty update(UUID primaryKey, CompanyProperty entity) {
		var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getKey());
		if (findGlobalName.isEmpty()) {
			throw new UnprocessableEntityExeption("Property key is not valid: " + entity.getKey());
		}
		return super.updateInternal(primaryKey, entity);
	}
	
	
	@Override
	public CompanyProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var oldProperty = repository.findById(primaryKey);
		if (oldProperty.isPresent()) {
			CompanyPropertyDTO testPropertyDTO = mapper.toDto(oldProperty.get());
			JsonNode original = objectMapper.valueToTree(testPropertyDTO);
			JsonNode patched = patch.apply(original);
			testPropertyDTO = objectMapper.treeToValue(patched, CompanyPropertyDTO.class);
			var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(testPropertyDTO.getKey());
			if (findGlobalName.isEmpty()) {
				throw new UnprocessableEntityExeption("Property key is not valid: " + testPropertyDTO.getKey());
			}
		}
		return super.partialUpdateInternal(primaryKey, patch);
		
	}
	
	@Override
	public Optional<CompanyProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	@Override
	public void delete(UUID primaryKey) {
		super.deleteInternal(primaryKey);
	}
	
}