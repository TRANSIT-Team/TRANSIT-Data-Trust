package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteOverviewDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.datalayers.domain.QCompanyAddress;
import com.transit.backend.datalayers.domain.QCompanyFavorite;
import com.transit.backend.datalayers.repository.CompanyFavoriteRepository;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.service.CompanyFavoriteService;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.CompanyFavoriteMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.CompanyFavoriteOverview;
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
public class CompanyFavoriteServiceBean implements CompanyFavoriteService {
	
	
	@Inject
	Validator validator;
	
	@Autowired
	private CompanyFavoriteRepository companyFavoriteRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private CompanyFavoriteMapper mapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	//securityBefore
	@Override
	public List<CompanyFavoriteOverview> getOverview(UUID companyId) {
		return companyFavoriteRepository.findAllByCompanyId(companyId).stream()
				.map(entry -> new CompanyFavoriteOverview(entry.getName(), entry.getId()))
				.toList();
	}
	
	@Override
	public CompanyFavorite create(UUID uuid, CompanyFavorite entity) {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		entity.setCompanyId(uuid);
		
		return mergeCompanyFavorites(uuid, companyFavoriteRepository.saveAndFlush(entity));
	}
	
	@Override
	public CompanyFavorite update(UUID uuid, UUID uuid2, CompanyFavorite entity) {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		entity.setCompanyId(uuid);
		var temp = companyFavoriteRepository.findByIdAndDeleted(uuid2, false);
		if (temp.isEmpty()) {
			throw new NoSuchElementFoundException("Cannot Found this favorite list.");
		}
		entity.setId(uuid2);
		entity.setCreateDate(temp.get().getCreateDate());
		entity.setCreatedBy(temp.get().getCreatedBy());
		return mergeCompanyFavorites(uuid, companyFavoriteRepository.saveAndFlush(entity));
	}
	
	@Override
	public CompanyFavorite partialUpdate(UUID uuid, UUID uuid2, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		var temp = companyFavoriteRepository.findByIdAndDeleted(uuid2, false);
		if (temp.isEmpty()) {
			throw new NoSuchElementFoundException("Cannot Found this favorite list.");
		}
		CompanyFavoriteDTO companyFavoriteDTO = mapper.toDto(temp.get());
		JsonNode original = objectMapper.valueToTree(companyFavoriteDTO);
		JsonNode patched = patch.apply(original);
		companyFavoriteDTO = objectMapper.treeToValue(patched, CompanyFavoriteDTO.class);
		
		Set<ConstraintViolation<CompanyFavoriteDTO>> violations = validator.validate(companyFavoriteDTO, ValidationGroups.Patch.class);
		if (violations.isEmpty()) {
			var companyFavoritePatched = mapper.toEntity(companyFavoriteDTO);
			companyFavoritePatched.setId(uuid2);
			companyFavoritePatched.setCompanyId(uuid);
			companyFavoritePatched.setCreateDate(temp.get().getCreateDate());
			companyFavoritePatched.setCreatedBy(temp.get().getCreatedBy());
			return mergeCompanyFavorites(uuid, companyFavoriteRepository.saveAndFlush(companyFavoritePatched));
		}
		throw new ConstraintViolationException(new HashSet<>(violations));
		
	}
	
	//security by query
	@Override
	public Collection<CompanyFavorite> read(UUID uuid, String query, FilterExtra collectionFilterExtra) {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		if (query.trim().isBlank()) {
			query += "companyId==" + userHelperFunctions.getCompanyId() + ";deleted==false";
		} else {
			query = "( " + query + " ) and companyId==" + userHelperFunctions.getCompanyId() + ";deleted==false";
		}
		var spec = RSQLQueryDslSupport.toPredicate(query, QCompanyFavorite.companyFavorite);
		
		return StreamSupport
				.stream(companyFavoriteRepository.findAll(spec).spliterator(), false)
				.collect(Collectors.toCollection(TreeSet::new));
		
	}
	
	@Override
	public Optional<CompanyFavorite> readOne(UUID uuid, UUID uuid2) {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		return companyFavoriteRepository.findByIdAndDeleted(uuid2, false);
	}
	
	@Override
	public void delete(UUID uuid, UUID uuid2) {
		if (!companyRepository.existsById(uuid)) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), uuid);
		}
		var temp = companyFavoriteRepository.findById(uuid2);
		if (temp.isEmpty()) {
			throw new NoSuchElementFoundException(CompanyFavorite.class.getSimpleName(), uuid2);
		}
		temp.get().setDeleted(true);
		companyFavoriteRepository.saveAndFlush(temp.get());
	}
	
	public CompanyFavorite mergeCompanyFavorites(UUID companyId, CompanyFavorite companyFavorite) {
		var allWithName = companyFavoriteRepository.findAllByCompanyIdAndNameAndDeleted(companyId, companyFavorite.getName(), false);
		if (allWithName.size() > 1) {
			allWithName.forEach(entry -> {
				companyFavorite.getCompanyList().addAll(entry.getCompanyList());
				if (!entry.getId().equals(companyFavorite.getId())) {
					this.delete(companyId, entry.getId());
				}
			});
		}
		return companyFavoriteRepository.saveAndFlush(companyFavorite);
		
	}
}
