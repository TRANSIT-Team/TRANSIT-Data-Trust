package com.transit.backend.datalayers.service.helper.abstractclasses;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetById<entity extends AbstractEntity> {
	
	
	public entity getEntity(entity entry, AbstractRepository<entity> abstractRepository, String className) {
		var opt = abstractRepository.findById(entry.getId());
		if (opt.isEmpty()) {
			throw new NoSuchElementFoundException(className, entry.getId());
		}
		return opt.get();
		//	.filter(out -> !out.isDeleted()).orElseThrow(() -> new NoSuchElementFoundException(className, entry.getId()));
	}
	
	public entity getEntity(UUID uuid, AbstractRepository<entity> abstractRepository, String className) {
		var opt = abstractRepository.findById(uuid);
		if (opt.isEmpty()) {
			throw new NoSuchElementFoundException(className, uuid);
		}
		return opt.get();
		
		//	.filter(out -> !out.isDeleted()).orElseThrow(() -> new NoSuchElementFoundException(className, uuid));
	}
	
	
}
