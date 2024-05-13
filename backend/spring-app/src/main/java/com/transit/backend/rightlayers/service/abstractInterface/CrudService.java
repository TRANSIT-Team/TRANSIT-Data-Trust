package com.transit.backend.rightlayers.service.abstractInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.Collection;
import java.util.Optional;

public interface CrudService<E, P> {
	
	E create(E entity);
	
	E update(P primaryKey, E entity);
	
	E partialUpdate(P primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	Collection<E> read(String query);
	
	Optional<E> readOne(P primaryKey);
	
	
	void delete(P primaryKey);
	
	
}