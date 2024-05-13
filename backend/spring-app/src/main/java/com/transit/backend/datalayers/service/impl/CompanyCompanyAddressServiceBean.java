package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.CompanyAddressRepository;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.service.CompanyCompanyAddressService;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
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
public class CompanyCompanyAddressServiceBean implements CompanyCompanyAddressService {
	
	@Inject
	Validator validator;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CompanyAddressMapper mapper;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	
	@Override
	public CompanyAddress create(UUID companyId, CompanyAddress entity) {
		
		
		return companyRepository
				.findById(companyId)
				.map(
						p -> {
							entity.setAddress(addressRepository.saveAndFlush(entity.getAddress()));
							entity.setId(new CompanyAddressId(companyId, entity.getAddress().getId()));
							entity.setCompany(p);
							CompanyAddress companyAddress = companyAddressRepository.saveAndFlush(entity);
							
							//p.addCompanyAddress(companyAddress);
							//companyRepository.saveAndFlush(p);
							
							return companyAddress;
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
	}
	
	@Override
	public CompanyAddress update(UUID companyId, UUID addressId, CompanyAddress entity) {
		return companyRepository
				.findByIdAndDeleted(companyId, false)
				.map(
						p -> p.getCompanyAddresses()
								.stream()
								.filter(companyAddress -> companyAddress.getId().equals(new CompanyAddressId(companyId, addressId)))
								.filter(companyAddress -> !companyAddress.isDeleted())
								.findAny()
								.map(
										companyAddress -> {
											putPatchValidatorProperties.validator(Address.class, rightsService.getAccess(addressId), companyAddress.getAddress(), entity.getAddress());
											entity.getAddress().setId(addressId);
											entity.getAddress().setCreateDate(addressRepository.getReferenceById(companyAddress.getId().getAddressId()).getCreateDate());
											entity.getAddress().setCreatedBy(addressRepository.getReferenceById(companyAddress.getId().getAddressId()).getCreatedBy());
											entity.setAddress(setBooleanShow(addressId, entity.getAddress()));
											entity.setAddress(addressRepository.saveAndFlush(entity.getAddress()));
											entity.setCompany(p);
											entity.setId(new CompanyAddressId(companyId, addressId));
											entity.setCreateDate(companyAddress.getCreateDate());
											entity.setCreatedBy(companyAddress.getCreatedBy());
											return companyAddressRepository.saveAndFlush(entity);
										})
								.orElseThrow(() -> new NoSuchElementFoundOrDeleted(CompanyAddress.class.getSimpleName(), new CompanyAddressId(companyId, addressId)))
				)
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Company.class.getSimpleName(), companyId));
		
	}
	
	public Address setBooleanShow(UUID primaryKey, Address address) {
		var oldEntity = addressRepository.findById(primaryKey);
		address.setShowOverviewFilter(oldEntity.get().getShowOverviewFilter());
		return address;
	}
	
	
	@Override
	public CompanyAddress partialUpdate(UUID companyId, UUID addressId, JsonMergePatch patch) {
		return companyRepository
				.findByIdAndDeleted(companyId, false)
				.map(
						p ->
								p.getCompanyAddresses()
										.stream()
										.filter(companyAddress -> companyAddress.getId().equals(new CompanyAddressId(companyId, addressId)))
										.filter(companyAddress -> !companyAddress.isDeleted())
										.findAny()
										.map(
												companyAddress -> {
													try {
														CompanyAddressDTO companyAddressDTO = mapper.toDto(companyAddress);
														JsonNode original = objectMapper.valueToTree(companyAddressDTO);
														JsonNode patched = patch.apply(original);
														companyAddressDTO = objectMapper.treeToValue(patched, CompanyAddressDTO.class);
														
														Set<ConstraintViolation<CompanyAddressDTO>> violations = validator.validate(companyAddressDTO, ValidationGroups.Patch.class);
														if (violations.isEmpty()) {
															var companyAddressPatched = mapper.toEntity(companyAddressDTO);
															putPatchValidatorProperties.validator(Address.class, rightsService.getAccess(addressId), companyAddress.getAddress(), companyAddressPatched.getAddress());
															companyAddressPatched.setId(new CompanyAddressId(companyId, addressId));
															companyAddressPatched.setCompany(companyAddress.getCompany());
															companyAddressPatched.setAddress(setBooleanShow(addressId, companyAddressPatched.getAddress()));
															companyAddressPatched.setAddress(addressRepository.saveAndFlush(companyAddressPatched.getAddress()));
															return companyAddressRepository.saveAndFlush(companyAddressPatched);
														} else {
															throw new ConstraintViolationException(
																	new HashSet<>(violations));
														}
													} catch (JsonPatchException | JsonProcessingException e) {
														throw new UnprocessableEntityExeption(e.getMessage());
													}
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(CompanyAddress.class.getSimpleName(), new CompanyAddressId(companyId, addressId))))
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Company.class.getSimpleName(), companyId));
		
	}
	
	@Override
	public Collection<CompanyAddress> read(UUID companyId, String query, FilterExtra pageParams) {
		if (query.trim().isBlank()) {
			query += "company.id==" + companyId;
		} else {
			query = "( " + query + " ) and company.id==" + companyId;
		}
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QCompanyAddress.companyAddress);
		companyRepository.findById(companyId).orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
		Pageable pageable;
		if (pageParams.isUseOtherParameters()) {
			pageable = new OffsetBasedPageRequest(pageParams.getTake(), pageParams.getSkip(), true);
			return new TreeSet<>(companyAddressRepository.findAll(spec, pageable).toList());
		} else {
			return StreamSupport
					.stream(companyAddressRepository.findAll(spec).spliterator(), false)
					.collect(Collectors.toCollection(TreeSet::new));
		}
	}
	
	@Override
	public Optional<CompanyAddress> readOne(UUID companyId, UUID addressId) {
		
		return companyRepository.findById(companyId).map(
						p ->
								Optional.of(
										p
												.getCompanyAddresses()
												.stream()
												.filter(companyAddress -> companyAddress.getId().equals(new CompanyAddressId(companyId, addressId)))
												.findAny()
												.orElseThrow(() -> new NoSuchElementFoundException(CompanyAddress.class.getSimpleName(), new CompanyAddressId(companyId, addressId)))
								))
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
		
	}
	
	
	@Override
	public void delete(UUID companyId, UUID addressId) {
		companyRepository
				.findById(companyId)
				.map(
						p ->
								p.getCompanyAddresses()
										.stream()
										.filter(companyAddress -> companyAddress.getId().equals(new CompanyAddressId(companyId, addressId)))
										.filter(companyAddress -> !companyAddress.isDeleted())
										.findAny()
										.map(
												companyAddress -> {
													companyAddress.setDeleted(true);
													companyAddressRepository.saveAndFlush(companyAddress);
													return Optional.empty();
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(CompanyAddress.class.getSimpleName(), new CompanyAddressId(companyId, addressId))))
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyId));
	}
	
	
}

