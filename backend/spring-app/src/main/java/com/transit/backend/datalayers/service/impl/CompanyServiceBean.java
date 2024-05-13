package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CompanyCompanyPropertyService;
import com.transit.backend.datalayers.service.CompanyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
import com.transit.backend.datalayers.service.mapper.CompanyMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.transferentities.FilterExtra;
import org.keycloak.admin.client.resource.UsersResource;
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


@Service
public class CompanyServiceBean extends CrudServiceExtendPropertyAbstract<Company, CompanyDTO, CompanyProperty, CompanyPropertyDTO> implements CompanyService {
	
	@Inject
	Validator validator;
	@Autowired
	private CompanyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CompanyMapper mapper;
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	@Autowired
	private CompanyPropertyMapper companyPropertyMapper;
	@Autowired
	private CompanyCompanyPropertyService companyCompanyPropertyService;
	@Autowired
	private CompanyAddressMapper companyAddressMapper;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private UserPropertyRepository userPropertyRepository;
	@Autowired
	private CompanyDeliveryAreaRepository companyDeliveryAreaRepository;
	private UsersResource usersResource;
	@Autowired
	private GlobalCompanyPropertiesRepository globalCompanyPropertiesRepository;
	@Autowired
	private DefaultSharingRightsRepository defaultSharingRightsRepository;
	
	public CompanyServiceBean(@Autowired KeycloakServiceManager keycloakServiceManager) {
		this.usersResource = keycloakServiceManager.getUsersResource();
	}
	
	@Override
	public CrudServiceSubRessource<CompanyProperty, UUID, UUID> getPropertySubService() {
		return this.companyCompanyPropertyService;
	}
	
	@Override
	public AbstractRepository<CompanyProperty> getPropertyRepository() {
		return this.companyPropertyRepository;
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getPropertyMapper() {
		return this.companyPropertyMapper;
	}
	
	@Override
	public AbstractRepository<Company> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<Company, CompanyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "companyProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteCompanyToCompanyProperty(m);
	}
	
	@Override
	public Company create(Company entity) {
		for (var property : entity.getProperties()) {
			var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(property.getKey());
			if (findGlobalName.isEmpty()) {
				throw new UnprocessableEntityExeption("Property key is not valid: " + property.getKey());
			}
		}
		entity = super.createInternal(entity);
		var companyDeliveryArea = new CompanyDeliveryArea();
		entity = super.saveInternal(entity);
		companyDeliveryArea.setCompany(entity);
		companyDeliveryAreaRepository.saveAndFlush(companyDeliveryArea);
		var companyDefaultSharProperties = new DefaultSharingRights();
		companyDefaultSharProperties.setId(entity.getId());
		companyDefaultSharProperties.setDefaultSharingRights("");
		defaultSharingRightsRepository.saveAndFlush(companyDefaultSharProperties);
		return entity;
		
	}
	
	@Override
	public Company update(UUID primaryKey, Company entity) {
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			for (var property : entity.getProperties()) {
				var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(property.getKey());
				if (findGlobalName.isEmpty()) {
					throw new UnprocessableEntityExeption("Property key is not valid: " + property.getKey());
				}
			}
			entity = super.updateInternal(primaryKey, entity);
			entity.setCompanyAddresses(oldEntity.get().getCompanyAddresses());
			entity.setCompanyUsers(oldEntity.get().getCompanyUsers());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	@Override
	public Class<Company> getEntityClazz() {
		return Company.class;
	}
	
	@Override
	public Class<CompanyDTO> getEntityDTOClazz() {
		return CompanyDTO.class;
	}
	
	@Override
	public Path<Company> getQClazz() {
		return QCompany.company;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public Company partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		
		var com = partialUpdateIntern(primaryKey, patch);
		
		return super.filterPUTPATCHInternal(com);
	}
	
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Company partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		if (oldEntity.isPresent()) {
			CompanyDTO testPropertyDTO = mapper.toDto(oldEntity.get());
			JsonNode original = objectMapper.valueToTree(testPropertyDTO);
			JsonNode patched = patch.apply(original);
			testPropertyDTO = objectMapper.treeToValue(patched, CompanyDTO.class);
			for (var property : testPropertyDTO.getProperties()) {
				var findGlobalName = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(property.getKey());
				if (findGlobalName.isEmpty()) {
					throw new UnprocessableEntityExeption("Property key is not valid: " + property.getKey());
				}
			}
			
			
			var entityDto = super.partialUpdateSavePropertiesInternal(super.partialUpdateInternal(primaryKey, patch));
			//entityDto.setCompanyAddresses(oldEntity.get().getCompanyAddresses().stream().map(companyAddressMapper::toDto).collect(Collectors.toCollection(TreeSet::new)));
			//entityDto.setCompanyUsers(oldEntity.get().getCompanyUsers().stream().map(userMapper::toDto).collect(Collectors.toCollection(TreeSet::new)));
			var entity = super.checkviolationsInternal(primaryKey, entityDto);
			entity.setCompanyAddresses(oldEntity.get().getCompanyAddresses());
			entity.setCompanyUsers(oldEntity.get().getCompanyUsers());
			return super.filterPUTPATCHInternal(super.saveInternal(entity));
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
		//return super.saveInternal(setForeignKey(super.checkviolationsInternal(primaryKey, super.partialUpdateSavePropertiesInternal(super.partialUpdateInternal(primaryKey, patch)))));
	}
	
	
	//NOo Users and Addresses in DTO, so only filter properties in Abstract class
	@Override
	public Page<Company> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Company> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	
	//STackOverflow
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(deleteInternal(super.deleteInternal(primaryKey)));
	}
	
	
	private Company deleteInternal(Company com) {
		if (com.getCompanyAddresses() != null) {
			com.getCompanyAddresses().stream().peek(cA -> {
				cA.setDeleted(true);
				companyAddressRepository.saveAndFlush(cA);
				cA.getAddress().setDeleted(true);
				addressRepository.saveAndFlush(cA.getAddress());
			});
		}
		if (com.getCompanyUsers() != null) {
			com.getCompanyUsers().stream().peek(cU -> {
				cU.setDeleted(true);
				userRepository.saveAndFlush(cU);
				var user = usersResource.get(cU.getKeycloakId().toString()).toRepresentation();
				user.setEnabled(false);
				usersResource.get(cU.getKeycloakId().toString()).update(user);
				if (cU.getUserProperties() != null) {
					for (var property : cU.getProperties()) {
						property.setDeleted(true);
						userPropertyRepository.saveAndFlush(property);
					}
				}
			});
		}
		
		
		return com;
	}
	
	
	@Override
	public Optional<DefaultSharingRights> getDefaultSharingRights(UUID companyId) {
		
		return defaultSharingRightsRepository.findById(companyId);
	}
	
	@Override
	public DefaultSharingRights setDefaultSharingRights(DefaultSharingRights defaultSharingRights) {
		return defaultSharingRightsRepository.saveAndFlush(defaultSharingRights);
	}
	
	
}
