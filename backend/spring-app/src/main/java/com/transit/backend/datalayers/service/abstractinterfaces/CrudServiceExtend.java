package com.transit.backend.datalayers.service.abstractinterfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CrudServiceExtend<E, P> {
	
	E create(E entity);
	
	E update(P primaryKey, E entity);
	
	E partialUpdate(P primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	Page<E> read(FilterExtra pageable, String query);
	
	Optional<E> readOne(P primaryKey);
	
	
	void delete(P primaryKey);
	
	
}