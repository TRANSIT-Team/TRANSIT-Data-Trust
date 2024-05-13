package com.transit.backend.datalayers.controller.abstractinterfaces;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.helper.verification.ValidationGroups;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

public interface CrudControllerSubResource<testDTO, Keyroot, Keynested> {
	
	@PostMapping
	ResponseEntity<testDTO> create(@PathVariable Keyroot rootId, @RequestBody @Validated(ValidationGroups.Post.class) testDTO dto);
	
	@PutMapping("/{nestedId}")
	ResponseEntity<testDTO> update(
			@PathVariable Keyroot rootId, @PathVariable Keynested nestedId, @RequestBody @Validated(ValidationGroups.Put.class) testDTO dto) throws ClassNotFoundException;
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{nestedId}", consumes = "application/merge-patch+json")
	ResponseEntity<testDTO> partialUpdate(@PathVariable Keyroot rootId, @PathVariable Keynested nestedId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException;
	
	
	@GetMapping
	ResponseEntity<CollectionModel<testDTO>> read(@PathVariable Keyroot rootId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                              @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                              @RequestParam(name = "take", defaultValue = "0") int take,
	                                              @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                              @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany
	
	);
	
	@GetMapping("/{nestedId}")
	ResponseEntity<testDTO> readOne(@PathVariable Keyroot rootId, @PathVariable Keynested nestedId);
	
	
	@DeleteMapping("/{nestedId}")
	ResponseEntity delete(@PathVariable Keyroot rootId, @PathVariable Keynested nestedId);
	
	
}
