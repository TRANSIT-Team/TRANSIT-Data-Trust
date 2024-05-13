package com.transit.backend.datalayers.controller.abstractinterfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface CrudControllerExtend<O, P> extends CrudControllerNested<O, P> {
	
	@PostMapping
	ResponseEntity<O> create(O object);
	
	@GetMapping
	ResponseEntity<PagedModel<O>> read(@PageableDefault(page = 0, size = Integer.MAX_VALUE) Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                   @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany,
	                                   @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                   @RequestParam(name = "take", defaultValue = "0") int take,
	                                   @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters);
	
	
}