package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.domain.QCompanyDeliveryArea;
import com.transit.backend.datalayers.repository.CompanyDeliveryAreaRepository;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.service.CompanyDeliveryAreaService;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.CompanyDeliveryAreaMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CompanyDeliveryAreaServiceBean implements CompanyDeliveryAreaService {
	
	@Inject
	Validator validator;
	@Autowired
	private CompanyDeliveryAreaRepository companyDeliveryAreaRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CompanyDeliveryAreaMapper mapper;
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	
	
	@Override
	public CompanyDeliveryArea create(UUID companyId, CompanyDeliveryArea entity) {
		return companyRepository
				.findById(companyId)
				.map(
						p -> {
							if (p.getCompanyDeliveryArea() == null) {
								entity.setCompany(p);
								var companyCompanyDeliveryArea = companyDeliveryAreaRepository.saveAndFlush(entity);
								return companyCompanyDeliveryArea;
							} else {
								throw new UnprocessableEntityExeption("CompanyDeliveryArea for Company already exists.");
							}
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
	}
	
	@Override
	public CompanyDeliveryArea update(UUID companyId, UUID companyDeliveryAreaId, CompanyDeliveryArea entity) {
		return companyRepository
				.findByIdAndDeleted(companyId, false)
				.map(
						p -> {
							var companyCompanyDeliveryArea = p.getCompanyDeliveryArea();
							putPatchValidatorProperties.validator(CompanyDeliveryArea.class, rightsService.getAccess(companyDeliveryAreaId), p.getCompanyDeliveryArea(), entity);
							entity.setId(companyDeliveryAreaId);
							entity.setCreateDate(companyDeliveryAreaRepository.getReferenceById(companyCompanyDeliveryArea.getId()).getCreateDate());
							entity.setCreatedBy(companyDeliveryAreaRepository.getReferenceById(companyCompanyDeliveryArea.getId()).getCreatedBy());
							entity.setCompany(p);
							return companyDeliveryAreaRepository.saveAndFlush(entity);
						})
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Company.class.getSimpleName(), companyId));
		
	}
	
	@Override
	public CompanyDeliveryArea partialUpdate(UUID companyId, UUID companyDeliveryAreaId, JsonMergePatch patch) {
		return companyRepository
				.findByIdAndDeleted(companyId, false)
				.map(
						p -> {
							var companyCompanyDeliveryArea = p.getCompanyDeliveryArea();
							try {
								CompanyDeliveryAreaDTO companyCompanyDeliveryAreaDTO = mapper.toDto(companyCompanyDeliveryArea);
								JsonNode original = objectMapper.valueToTree(companyCompanyDeliveryAreaDTO);
								JsonNode patched = patch.apply(original);
								companyCompanyDeliveryAreaDTO = objectMapper.treeToValue(patched, CompanyDeliveryAreaDTO.class);
								
								Set<ConstraintViolation<CompanyDeliveryAreaDTO>> violations = validator.validate(companyCompanyDeliveryAreaDTO, ValidationGroups.Patch.class);
								if (violations.isEmpty()) {
									var companyCompanyDeliveryAreaPatched = mapper.toEntity(companyCompanyDeliveryAreaDTO);
									putPatchValidatorProperties.validator(CompanyDeliveryArea.class, rightsService.getAccess(companyDeliveryAreaId), p.getCompanyDeliveryArea(), companyCompanyDeliveryAreaPatched);
									companyCompanyDeliveryAreaPatched.setId(companyDeliveryAreaId);
									companyCompanyDeliveryAreaPatched.setCompany(p);
									return companyDeliveryAreaRepository.saveAndFlush(companyCompanyDeliveryAreaPatched);
								} else {
									throw new ConstraintViolationException(
											new HashSet<>(violations));
								}
							} catch (JsonPatchException | JsonProcessingException e) {
								throw new UnprocessableEntityExeption(e.getMessage());
							}
						})
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Company.class.getSimpleName(), companyId));
		
	}
	
	@Override
	public Collection<CompanyDeliveryArea> read(UUID companyId, String query, FilterExtra pageParams) {
		if (query.trim().isBlank()) {
			query += "company.id==" + companyId;
		} else {
			query = "( " + query + " ) and company.id==" + companyId;
		}
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QCompanyDeliveryArea.companyDeliveryArea);
		companyRepository.findById(companyId).orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
		Pageable pageable;
		if (pageParams.isUseOtherParameters()) {
			pageable = new OffsetBasedPageRequest(pageParams.getTake(), pageParams.getSkip(), true);
			return new TreeSet<>(companyDeliveryAreaRepository.findAll(spec, pageable).toList());
		} else {
			return StreamSupport
					.stream(companyDeliveryAreaRepository.findAll(spec).spliterator(), false)
					.collect(Collectors.toCollection(TreeSet::new));
		}
	}
	
	@Override
	public Optional<CompanyDeliveryArea> readOne(UUID companyId, UUID companyDeliveryAreaId) {
		
		var company = companyRepository.findById(companyId);
		var deliveryArea = company.map(Company::getCompanyDeliveryArea);
		return deliveryArea;
	}
	
	
	@Override
	public void delete(UUID companyId, UUID companyDeliveryAreaId) {
		companyRepository
				.findById(companyId)
				.map(
						p -> {
							
							var companyCompanyDeliveryArea = companyDeliveryAreaRepository.findById(p.getCompanyDeliveryArea().getId()).get();
							companyCompanyDeliveryArea.getDeliveryAreaPolyline();
							companyCompanyDeliveryArea.setDeleted(true);
							companyDeliveryAreaRepository.saveAndFlush(companyCompanyDeliveryArea);
							return Optional.empty();
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
	}
}
	

	