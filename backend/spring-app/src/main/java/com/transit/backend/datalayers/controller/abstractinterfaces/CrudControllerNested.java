package com.transit.backend.datalayers.controller.abstractinterfaces;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface CrudControllerNested<testDTO, Key> {
	
	
	@PutMapping("/{id}")
	ResponseEntity<testDTO> update(@PathVariable("id") Key primaryKey, testDTO object) throws ClassNotFoundException;
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{id}", consumes = "application/merge-patch+json")
	ResponseEntity<testDTO> partialUpdate(@PathVariable("id") Key primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException;
	
	@GetMapping(path = "/{id}")
	ResponseEntity<testDTO> readOne(@PathVariable("id") Key primaryKey);
	
	
	@DeleteMapping("/{id}")
	ResponseEntity delete(@PathVariable("id") Key primaryKey);
	
}