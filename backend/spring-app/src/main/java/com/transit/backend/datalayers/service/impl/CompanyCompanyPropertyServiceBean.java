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
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.repository.GlobalCompanyPropertiesRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CompanyCompanyPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyCompanyPropertyServiceBean extends CrudServiceSubRessourceAbstract<CompanyProperty, CompanyPropertyDTO, Company> implements CompanyCompanyPropertyService {
	
	@Inject
	Validator validator;
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CompanyPropertyMapper mapper;
	@Autowired
	private GlobalCompanyPropertiesRepository globalCompanyPropertiesRepository;
	
	@Override
	public AbstractRepository<CompanyProperty> getPropertyRepository() {
		return this.companyPropertyRepository;
	}
	
	@Override
	public AbstractRepository<Company> getParentRepository() {
		return this.companyRepository;
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getPropertyMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<CompanyProperty> getPropertyClazz() {
		return CompanyProperty.class;
	}
	
	@Override
	public Class<CompanyPropertyDTO> getPropertyDTOClazz() {
		return CompanyPropertyDTO.class;
	}
	
	@Override
	public Class<Company> getParentClass() {
		return Company.class;
	}
	
	@Override
	public Path<CompanyProperty> getPropertyQClazz() {
		return QCompanyProperty.companyProperty;
	}
	
	@Override
	public String getParentString() {
		return "company";
	}
	
	@Override
	public CompanyProperty create(UUID companyId, CompanyProperty entity) {
		var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getKey());
		if (findGlobalName.isEmpty()) {
			throw new UnprocessableEntityExeption("Property key is not valid: " + entity.getKey());
		}
		return super.createInternal(companyId, entity);
	}
	
	@Override
	public CompanyProperty update(UUID companyId, UUID companyPropertyId, CompanyProperty entity) {
		var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(entity.getKey());
		if (findGlobalName.isEmpty()) {
			throw new UnprocessableEntityExeption("Property key is not valid: " + entity.getKey());
		}
		return super.updateInternal(companyId, companyPropertyId, entity);
	}
	
	@Override
	public CompanyProperty partialUpdate(UUID companyId, UUID companyPropertyId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var oldProperty = companyPropertyRepository.findById(companyPropertyId);
		if (oldProperty.isPresent()) {
			CompanyPropertyDTO testPropertyDTO = getPropertyMapper().toDto(oldProperty.get());
			JsonNode original = objectMapper.valueToTree(testPropertyDTO);
			JsonNode patched = patch.apply(original);
			testPropertyDTO = objectMapper.treeToValue(patched, getPropertyDTOClazz());
			var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(testPropertyDTO.getKey());
			if (findGlobalName.isEmpty()) {
				throw new UnprocessableEntityExeption("Property key is not valid: " + testPropertyDTO.getKey());
			}
		}
		
		return super.partialUpdateInternal(companyId, companyPropertyId, patch);
	}
	
	@Override
	public Collection<CompanyProperty> read(UUID companyId, String query, FilterExtra collectionFilterExtra) {
		return super.readInternal(companyId, query, collectionFilterExtra);
	}
	
	@Override
	public Optional<CompanyProperty> readOne(UUID companyId, UUID companyPropertyId) {
		
		return super.readOneInternal(companyId, companyPropertyId);
		
	}
	
	
	@Override
	public void delete(UUID companyId, UUID companyPropertyId) {
		super.deleteInternal(companyId, companyPropertyId);
	}
}
	


