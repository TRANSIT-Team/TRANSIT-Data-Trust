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
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.rightlayers.service.AccessService;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class CrudServiceNestedAbstract<test extends AbstractPropertyEntity<parentTest>, testId, testDTO, parentTest extends AbstractParentEntity<test>> {
	
	@Inject
	Validator validator;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	@Autowired
	private AccessService rightsService;
	
	public test updateInternal(UUID primaryKey, test entity) {
		var original = getRepository().findById(primaryKey);
		if (original.isPresent()) {
			putPatchValidatorProperties.validator(getClazz(), rightsService.getAccess(primaryKey), original.get(), entity);
			entity.setId(original.get().getId());
			entity.setCreateDate(original.get().getCreateDate());
			entity.setParent(original.get().getParent());
			return getRepository().saveAndFlush(entity);
		} else {
			throw new NoSuchElementFoundException(getClazz().getSimpleName(), primaryKey);
		}
	}
	
	public abstract AbstractRepository<test> getRepository();
	
	public abstract Class<test> getClazz();
	
	public test partialUpdateInternal(UUID primaryKey, JsonMergePatch patch) {
		var property = getRepository().findById(primaryKey);
		if (property.isEmpty()) {
			throw new NoSuchElementFoundException(getClazz().getSimpleName(), primaryKey);
		} else {
			try {
				
				testDTO testDTO = getMapper().toDto(property.get());
				JsonNode original = objectMapper.valueToTree(testDTO);
				JsonNode patched = patch.apply(original);
				testDTO = objectMapper.treeToValue(patched, getDTOClazz());
				
				Set<ConstraintViolation<testDTO>> violations = validator.validate(testDTO);
				if (violations.isEmpty()) {
					test propertyPatched = getMapper().toEntity(testDTO);
					propertyPatched.setParent(property.get().getParent());
					putPatchValidatorProperties.validator(getClazz(), rightsService.getAccess(primaryKey), property.get(), propertyPatched);
					return getRepository().saveAndFlush(propertyPatched);
				} else {
					throw new ConstraintViolationException(
							new HashSet<>(violations));
				}
			} catch (JsonPatchException | JsonProcessingException e) {
				throw new BadRequestException(e.getMessage());
			}
		}
		
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public abstract Class<testDTO> getDTOClazz();
	
	public Optional<test> readOneInternal(UUID primaryKey) {
		var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(primaryKey), getQClazz());
		return getRepository().findOne(spec);
	}
	
	public abstract Path getQClazz();
	
	public void deleteInternal(UUID primaryKey) {
		getRepository().findById(primaryKey).filter(test -> !test.isDeleted()).map(property -> {
			property.setDeleted(true);
			getRepository().saveAndFlush(property);
			return Optional.empty();
		}).orElseThrow(() -> new NoSuchElementFoundOrDeleted(getClazz().getSimpleName(), primaryKey));
	}
	
}
