package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerNestedAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.UserPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.UserPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.UserPropertyMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.UserPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/userproperties")
public class UserPropertyController extends CrudControllerNestedAbstract<UserProperty, UUID, UserPropertyDTO, User, UserDTO, UserController, UserPropertyController> implements CrudControllerNested<UserPropertyDTO, UUID> {
	
	@Autowired
	private UserPropertyService service;
	
	@Autowired
	private UserPropertyMapper mapper;
	
	@Autowired
	private UserPropertyAssemblerWrapper userPropertyAssemblerWrapper;
	@Autowired
	private UserPropertyFilter userPropertyFilter;
	
	@PreAuthorize(
			"hasAnyAuthority()"
	)
	@Override
	public ResponseEntity<UserPropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) UserPropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public AbstractMapper<UserProperty, UserPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceNested<UserProperty, UUID> getService() {
		return this.service;
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<UserProperty, ?> getFilterHelper() {
		return this.userPropertyFilter;
	}
	
	@Override
	public AssemblerWrapperNestedAbstract<UserProperty, UserPropertyDTO, User, UserDTO, UserController, UserPropertyController> getAssemblerWrapper() {
		return this.userPropertyAssemblerWrapper;
	}
	
	@PreAuthorize(
			"hasAnyAuthority()"
	)
	@Override
	public ResponseEntity<UserPropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@PreAuthorize(
			"hasAnyAuthority()"
	)
	@Override
	
	public ResponseEntity<UserPropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	@PreAuthorize(
			"hasAnyAuthority()"
	)
	@Override
	public ResponseEntity delete(UUID primaryKey) {
		return super.delete(primaryKey);
	}
}
