package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerNestedAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehousePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.WarehousePropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.WarehousePropertyFilter;
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
@RequestMapping("/warehouseproperties")
public class WarehousePropertyController extends CrudControllerNestedAbstract<WarehouseProperty, UUID, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehousePropertyController> implements CrudControllerNested<WarehousePropertyDTO, UUID> {
	
	@Autowired
	private WarehousePropertyService warehousePropertyService;
	
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	
	@Autowired
	private WarehousePropertyAssemblerWrapper warehousePropertyAssemblerWrapper;
	@Autowired
	private WarehousePropertyFilter warehousePropertyFilter;
	
	@Override
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) WarehousePropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getMapper() {
		return this.warehousePropertyMapper;
	}
	
	@Override
	public CrudServiceNested<WarehouseProperty, UUID> getService() {
		return this.warehousePropertyService;
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<WarehouseProperty, ?> getFilterHelper() {
		return this.warehousePropertyFilter;
	}
	
	@Override
	public AssemblerWrapperNestedAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehousePropertyController> getAssemblerWrapper() {
		return this.warehousePropertyAssemblerWrapper;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
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
