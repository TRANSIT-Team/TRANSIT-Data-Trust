package com.transit.backend.datalayers.service.abstractclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdCanBeNull;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.LongestConditionName;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLOperators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
public abstract class CrudServiceExtendPropertyAbstract<test extends AbstractParentEntity<testProperty>, testDTO extends AbstractPropertiesParentDTO<testDTO, testPropertyDTO>, testProperty extends AbstractPropertyEntity<test>, testPropertyDTO extends AbstractDTOIdCanBeNull<testPropertyDTO>> extends CrudServiceExtendAbstract<test, testDTO> {
	
	
	private final int[] finalLongestConditionNameArray = {-1};
	@Inject
	Validator validator;
	@Autowired
	private ObjectMapper objectMapper;
	
	public abstract AbstractRepository<testProperty> getPropertyRepository();
	
	public abstract AbstractMapper<testProperty, testPropertyDTO> getPropertyMapper();
	
	public test saveInternal(test entity) {
		if (entity.getProperties() != null && !entity.getProperties().isEmpty()) {
			entity.getProperties().forEach(p -> p.setParent(entity));
		}
		return super.saveInternal(entity);
	}
	
	public abstract AbstractRepository<test> getRepository();
	
	public test createInternal(test entity) {
		entity = super.createInternal(entity);
		
		if (entity.getProperties() != null && !entity.getProperties().isEmpty()) {
			//check if duplicate keys exists
			duplicateKeyInternal(entity.getProperties());
			test finalEntity = entity;
			entity.getProperties().forEach(testProperties -> testProperties.setParent(finalEntity));
		}
		
		return entity;
	}
	
	public test updateInternal(UUID primaryKey, test entity) {
		var oldEntity = getRepository().findByIdAndDeleted(primaryKey, false);
		var updatedEntity = super.updateInternal(primaryKey, entity);
		if (oldEntity.isPresent()) {
			updatedEntity.setProperties(updatePropertiesInternal(oldEntity.get().getProperties(), entity.getProperties(), entity));
			return updatedEntity;
		} else {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		}
	}
	
	public testDTO partialUpdateInternal(UUID primaryKey, JsonMergePatch patch) {
		return partialUpdateSubMethod(primaryKey, patch);
	}
	
	public testDTO partialUpdateSubMethod(UUID primaryKey, JsonMergePatch patch) {
		Optional<test> test = getRepository().findByIdAndDeleted(primaryKey, false);
		if (test.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		} else {
			//Nur ein Update möglich, wenn noch nicht geteilt wurde, sonst werden die selben Daten übernommen oder es müssen die gleichen Einträge vorhanden sein
			var testDTOPatched = super.partialUpdateSubMethod(primaryKey, patch);
			
			testDTOPatched
					.setProperties(
							updatePropertiesInternal(
									test
											.get()
											.getProperties(),
									testDTOPatched
											.getProperties()
											.stream()
											.map(getPropertyMapper()::toEntity)
											.collect(Collectors.toCollection(TreeSet::new)),
									test
											.get()
							)
									.stream()
									.map(getPropertyMapper()::toDto).collect(Collectors.toCollection(TreeSet::new))
					);
			return testDTOPatched;
			
		}
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public test checkviolationsInternal(UUID primaryKey, testDTO dto) {
		return super.checkviolationsInternal(primaryKey, dto);
	}
	
	@Override
	public Page<test> readInternal(FilterExtra pageable, String query) {
		var preSelected = super.readInternal(pageable, query);
		int finalLongestConditionName = LongestConditionName.longestConditonInQuery(query);
		return preSelected.map(test -> {
			if (test.getProperties() == null || test.getProperties().isEmpty()) {
				return test;
			}
			String replaceQuery = getQueryRewritedString(QueryRewrite.queryDefaultMatcher(query, finalLongestConditionName));
			test.setProperties((SortedSet<testProperty>) getPropertySubService().read(test.getId(), replaceQuery, pageable));
			return test;
		});
		
	}
	
	public abstract String getQueryRewritedString(Matcher m);
	
	public abstract CrudServiceSubRessource<testProperty, UUID, UUID> getPropertySubService();
	
	@Override
	public Optional<test> readOneInternal(UUID primaryKey) {
		finalLongestConditionNameArrayFunction(finalLongestConditionNameArray);
		var returnStatement = super.readOneInternal(primaryKey).map(test -> {
			if (test.getProperties() == null || test.getProperties().isEmpty()) {
				return test;
			}
			//switch query Rewrite
			String replaceQuery = getQueryRewritedString(QueryRewrite.queryDefaultMatcher(getPropertyDeletedString(), finalLongestConditionNameArray[0]));
			test.setProperties((SortedSet<testProperty>) getPropertySubService().read(test.getId(), replaceQuery, GetFilterExtra.getEmptyFilterExtra()));
			return test;
		});
		return returnStatement;
	}
	
	static void finalLongestConditionNameArrayFunction(int[] finalLongestConditionNameArray) {
		if (finalLongestConditionNameArray[0] == -1) {
			RSQLOperators.supportedOperators().forEach(operator -> {
				for (var symbol : operator.getSymbols()) {
					if (symbol.replaceAll("=", "").length() > finalLongestConditionNameArray[0]) {
						finalLongestConditionNameArray[0] = symbol.replaceAll("=", "").length();
					}
				}
			});
		}
	}
	
	public abstract String getPropertyDeletedString();
	
	public test deleteInternal(UUID primaryKey) {
		
		test test = super.deleteInternal(primaryKey);
		test.getProperties().forEach(properties -> properties.setDeleted(true));
		return test;
	}
	
	private SortedSet<testProperty> updatePropertiesInternal(SortedSet<testProperty> original, SortedSet<testProperty> update, test test) {
		//Check if new List is empty
		//Check if old List is empty and set elements to deleted
		
		if (update == null || update.isEmpty()) {
			if (original != null && !original.isEmpty()) {
				original.forEach(testProperties -> testProperties.setDeleted(true));
			}
			if (original == null) {
				return new TreeSet<>();
			}
			return new TreeSet<>(original);
		}
		//check if duplicate key in update exists
		duplicateKeyInternal(update);
		
		//Old are empty and new not--> insert backward link from Property to test
		if (original == null || original.isEmpty()) {
			update.forEach(testProperties -> testProperties.setParent(test));
			return update;
		}
		//Check if Property exists and update/insert it
		SortedSet<testProperty> testPropertyUpdated = new TreeSet<>();
		update.forEach(testProperties -> {
			var result = original.stream().filter(testProperty -> testProperty.getKey().equals(testProperties.getKey())).findAny();
			if (result.isEmpty()) {
				testProperties.setParent(test);
				testPropertyUpdated.add(testProperties);
			} else {
				result.get().setValue(testProperties.getValue());
				result.get().setType(testProperties.getType());
				result.get().setDeleted(false);
				testPropertyUpdated.add(result.get());
			}
			
		});
		//Check if old Properties have to be deleted
		original.forEach(testProperties -> {
			var result = testPropertyUpdated.stream().filter(testProperty -> testProperty.getKey().equals(testProperties.getKey())).findAny();
			if (result.isEmpty()) {
				testProperties.setDeleted(true);
				testPropertyUpdated.add(testProperties);
			}
			
		});
		return testPropertyUpdated;
	}
	
	private void duplicateKeyInternal(SortedSet<testProperty> update) {
		update.stream().map(testProperty::getKey).map(key -> {
			if ((Collections.frequency(update.stream().map(testProperty::getKey).collect(Collectors.toList()), key)) > 1) {
				throw new UnprocessableEntityExeption("Duplicate key" + key);
			}
			return null;
		});
	}
	
	public testDTO partialUpdateSavePropertiesInternal(testDTO entity) {
		entity.setProperties(entity.getProperties().stream().map(getPropertyMapper()::toEntity).map(p -> {
			p.setParent(getMapper().toEntity(entity));
			return p;
		}).map(getPropertyRepository()::saveAndFlush).map(getPropertyMapper()::toDto).collect(Collectors.toCollection(TreeSet::new)));
		return entity;
	}
	
	public test filterPUTPATCHInternal(test test) {
		test
				.setProperties(
						test
								.getProperties()
								.stream()
								.filter(testProperties -> !testProperties.isDeleted())
								.collect(Collectors.toCollection(TreeSet::new)));
		return test;
	}
	
	
}