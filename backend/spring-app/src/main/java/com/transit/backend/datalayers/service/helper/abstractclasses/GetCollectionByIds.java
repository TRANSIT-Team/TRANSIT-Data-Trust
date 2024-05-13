package com.transit.backend.datalayers.service.helper.abstractclasses;


import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GetCollectionByIds<entity extends AbstractEntity, repo extends AbstractRepository<entity>> {
	
	
	public List<entity> updateList(List<UUID> original, List<UUID> update, repo repository, String className, GetById<entity> getEntity) {
		if (original != null && update != null) {
			Set<UUID> alreadyThereResult = update.stream().distinct().filter(original::contains).collect(Collectors.toSet());
			Set<UUID> newThereResult = update.stream().distinct().filter(uuid -> !original.contains(uuid)).collect(Collectors.toSet());
			List<entity> subEntities = new ArrayList<>();
			subEntities.addAll(alreadyThereResult.stream().map(uuid -> {
				var subEntity = getEntity.getEntity(uuid, repository, className);
				subEntity.setDeleted(false);
				
				return subEntity;
			}).toList());
			subEntities.addAll(newThereResult.stream().map(uuid -> getEntity.getEntity(uuid, repository, className)).toList());
			return subEntities;
		} else if (original != null) {
			return original.stream().map(uuid -> {
				var subEntity = getEntity.getEntity(uuid, repository, className);
				subEntity.setDeleted(true);
				return subEntity;
			}).toList();
		} else if (update != null) {
			return update.stream().map(uuid -> {
				return getEntity.getEntity(uuid, repository, className);
			}).toList();
		} else {
			return new ArrayList<>();
		}
		
	}
	
	public Set<entity> updateSet(List<UUID> original, List<UUID> update, repo repository, String className, GetById<entity> getEntity) {
		if (original != null && update != null) {
			Set<UUID> alreadyThereResult = update.stream().distinct().filter(original::contains).collect(Collectors.toSet());
			Set<UUID> newThereResult = update.stream().distinct().filter(uuid -> !original.contains(uuid)).collect(Collectors.toSet());
			Set<entity> subEntities = new TreeSet<>();
			subEntities.addAll(alreadyThereResult.stream().map(uuid -> {
				var subEntity = getEntity.getEntity(uuid, repository, className);
				subEntity.setDeleted(false);
				
				return subEntity;
			}).toList());
			subEntities.addAll(newThereResult.stream().map(uuid -> getEntity.getEntity(uuid, repository, className)).toList());
			return subEntities;
		} else if (original != null) {
			return original.stream().map(uuid -> {
				var subEntity = getEntity.getEntity(uuid, repository, className);
				subEntity.setDeleted(true);
				return subEntity;
			}).collect(Collectors.toSet());
		} else if (update != null) {
			return update.stream().map(uuid -> getEntity.getEntity(uuid, repository, className)).collect(Collectors.toSet());
		} else {
			return new TreeSet<>();
		}
		
	}
}





