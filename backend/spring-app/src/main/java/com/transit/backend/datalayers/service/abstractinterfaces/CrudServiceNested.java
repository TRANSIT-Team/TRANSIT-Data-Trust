package com.transit.backend.datalayers.service.abstractinterfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.Optional;

public interface CrudServiceNested<E, P> {
	
	E update(P primaryKey, E entity);
	
	E partialUpdate(P primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	
	Optional<E> readOne(P primaryKey);
	
	
	void delete(P primaryKey);
	
	
}