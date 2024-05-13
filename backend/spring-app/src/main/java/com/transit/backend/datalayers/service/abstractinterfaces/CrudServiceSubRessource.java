package com.transit.backend.datalayers.service.abstractinterfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.transferentities.FilterExtra;

import java.util.Collection;
import java.util.Optional;

public interface CrudServiceSubRessource<test, key, rootKey> {
	
	test create(rootKey rootKey, test entity);
	
	test update(rootKey rootKey, key key, test entity);
	
	test partialUpdate(rootKey rootKey, key key, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;
	
	Collection<test> read(rootKey rootKey, String query, FilterExtra collectionFilterExtra);
	
	Optional<test> readOne(rootKey rootKey, key key);
	
	
	void delete(rootKey rootKey, key key);
	
	
}