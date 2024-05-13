package com.transit.backend.datalayers.service.abstractclasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class CrudServiceSubRessourceAbstract<testProperty extends AbstractPropertyEntity<parent>, testPropertyDTO, parent extends AbstractParentEntity<testProperty>> {
	
	@Inject
	Validator validator;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	@Autowired
	private AccessService rightsService;
	
	public testProperty createInternal(UUID parentId, testProperty entity) {
		
		
		return getParentRepository()
				.findById(parentId)
				.map(
						p -> {
							entity.setParent(p);
							testProperty testProperty = getPropertyRepository().saveAndFlush(entity);
							p.addProperty(testProperty);
							getParentRepository().saveAndFlush(p);
							return testProperty;
						})
				.orElseThrow(() -> new NoSuchElementFoundException(getParentClass().getSimpleName(), parentId));
	}
	
	public abstract AbstractRepository<parent> getParentRepository();
	
	public abstract AbstractRepository<testProperty> getPropertyRepository();
	
	public abstract Class<parent> getParentClass();
	
	public testProperty updateInternal(UUID parentId, UUID testPropertyId, testProperty entity) {
		return getParentRepository()
				.findByIdAndDeleted(parentId, false)
				.map(
						p ->
								p.getProperties()
										.stream()
										.filter(testProperty -> testProperty.getId().equals(testPropertyId))
										.filter(testProperty -> !testProperty.isDeleted())
										.findAny()
										.map(
												testProperty -> {
													putPatchValidatorProperties.validator(getPropertyClazz(), rightsService.getAccess(testPropertyId), testProperty, entity);
													entity.setId(testPropertyId);
													entity.setCreateDate(testProperty.getCreateDate());
													entity.setParent(p);
													return getPropertyRepository().saveAndFlush(entity);
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(getPropertyClazz().getSimpleName(), testPropertyId)))
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(getParentClass().getSimpleName(), parentId));
		
	}
	
	public abstract Class<testProperty> getPropertyClazz();
	
	public testProperty partialUpdateInternal(UUID parentId, UUID testPropertyId, JsonMergePatch patch) {
		return getParentRepository()
				.findByIdAndDeleted(parentId, false)
				.map(
						p ->
								p.getProperties()
										.stream()
										.filter(testProperty -> testProperty.getId().equals(testPropertyId))
										.filter(testProperty -> !testProperty.isDeleted())
										.findAny()
										.map(
												testProperty -> {
													try {
														testPropertyDTO testPropertyDTO = getPropertyMapper().toDto(testProperty);
														JsonNode original = objectMapper.valueToTree(testPropertyDTO);
														JsonNode patched = patch.apply(original);
														testPropertyDTO = objectMapper.treeToValue(patched, getPropertyDTOClazz());
														
														Set<ConstraintViolation<testPropertyDTO>> violations = validator.validate(testPropertyDTO, ValidationGroups.Patch.class);
														if (violations.isEmpty()) {
															var testPropertyPatched = getPropertyMapper().toEntity(testPropertyDTO);
															testPropertyPatched.setParent(testProperty.getParent());
															putPatchValidatorProperties.validator(getPropertyClazz(), rightsService.getAccess(testPropertyId), testProperty, testPropertyPatched);
															return getPropertyRepository().saveAndFlush(testPropertyPatched);
															
														} else {
															throw new ConstraintViolationException(
																	new HashSet<>(violations));
														}
													} catch (JsonPatchException | JsonProcessingException e) {
														throw new UnprocessableEntityExeption(e.getMessage());
													}
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(getPropertyClazz().getSimpleName(), testPropertyId)))
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(getParentClass().getSimpleName(), parentId));
		
	}
	
	public abstract AbstractMapper<testProperty, testPropertyDTO> getPropertyMapper();
	
	public abstract Class<testPropertyDTO> getPropertyDTOClazz();
	
	//convert to camel case how
	public Collection<testProperty> readInternal(UUID parentId, String query, FilterExtra collectionFilterExtra) {
		if (query.trim().isBlank()) {
			query += getParentString() + ".id==" + parentId;
		} else {
			query = "( " + query + " ) and " + getParentString() + ".id==" + parentId;
		}
		var spec = RSQLQueryDslSupport.toPredicate(query, getPropertyQClazz());
		getParentRepository().findById(parentId).orElseThrow(() -> new NoSuchElementFoundException(getParentClass().getSimpleName(), parentId));
		return StreamSupport
				.stream(getPropertyRepository().findAll(spec).spliterator(), false)
				.collect(Collectors.toCollection(TreeSet::new));
	}
	
	public abstract String getParentString();
	
	public abstract Path<testProperty> getPropertyQClazz();
	
	public Optional<testProperty> readOneInternal(UUID parentId, UUID testPropertyId) {
		
		return getParentRepository().findById(parentId).map(
						p ->
								Optional.of(
										p
												.getProperties()
												.stream()
												.filter(testProperty -> testProperty.getId().equals(testPropertyId))
												.findAny()
												.orElseThrow(() -> new NoSuchElementFoundException(getPropertyClazz().getSimpleName(), testPropertyId))
								))
				.orElseThrow(() -> new NoSuchElementFoundException(getParentClass().getSimpleName(), parentId));
		
	}
	
	
	public void deleteInternal(UUID parentId, UUID testPropertyId) {
		getParentRepository()
				.findById(parentId)
				.map(
						p ->
								p.getProperties()
										.stream()
										.filter(testProperty -> testProperty.getId().equals(testPropertyId))
										.filter(testProperty -> !testProperty.isDeleted())
										.findAny()
										.map(
												testProperty -> {
													testProperty.setDeleted(true);
													testProperty.setParent(p);
													getPropertyRepository().saveAndFlush(testProperty);
													return Optional.empty();
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(getPropertyClazz().getSimpleName(), testPropertyId)))
				.orElseThrow(() -> new NoSuchElementFoundException(getParentClass().getSimpleName(), parentId));
	}
	
	
}

