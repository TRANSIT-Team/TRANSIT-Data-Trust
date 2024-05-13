package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerNestedAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.CarPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CarPropertyFilter;
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
@RequestMapping("/carproperties")
public class CarPropertyController extends CrudControllerNestedAbstract<CarProperty, UUID, CarPropertyDTO, Car, CarDTO, CarController, CarPropertyController> implements CrudControllerNested<CarPropertyDTO, UUID> {
	
	@Autowired
	private CarPropertyService carPropertyService;
	
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	
	@Autowired
	private CarPropertyAssemblerWrapper carPropertyAssemblerWrapper;
	@Autowired
	private CarPropertyFilter carPropertyFilter;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) CarPropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getMapper() {
		return this.carPropertyMapper;
	}
	
	@Override
	public CrudServiceNested<CarProperty, UUID> getService() {
		return this.carPropertyService;
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<CarProperty, ?> getFilterHelper() {
		return this.carPropertyFilter;
	}
	
	@Override
	public AssemblerWrapperNestedAbstract<CarProperty, CarPropertyDTO, Car, CarDTO, CarController, CarPropertyController> getAssemblerWrapper() {
		return this.carPropertyAssemblerWrapper;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity delete(UUID primaryKey) {
		return super.delete(primaryKey);
	}
}
