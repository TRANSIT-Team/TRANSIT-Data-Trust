package com.transit.backend.datalayers.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerNestedAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.PackagePackagePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.PackageItemPackagePropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.PackagePackagePropertyFilter;
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
@RequestMapping("/packagepackageproperties")
public class PackagePackagePropertyController extends CrudControllerNestedAbstract<PackagePackageProperty, UUID, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackagePackagePropertyController> implements CrudControllerNested<PackagePackagePropertyDTO, UUID> {
	
	
	@Autowired
	private PackageItemPackagePropertyService service;
	
	@Autowired
	private PackagePackagePropertyMapper mapper;
	
	@Autowired
	private PackagePackagePropertyAssemblerWrapper packagePropertiesAssembler;
	@Autowired
	private PackagePackagePropertyFilter packagePackagePropertyFilter;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) PackagePackagePropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceNested<PackagePackageProperty, UUID> getService() {
		return this.service;
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<PackagePackageProperty, ?> getFilterHelper() {
		return this.packagePackagePropertyFilter;
	}
	
	@Override
	public AssemblerWrapperNestedAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackagePackagePropertyController> getAssemblerWrapper() {
		return this.packagePropertiesAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	
	@Override
	@PreAuthorize("@securityEntityService.hasAnyAuthority()")
	public ResponseEntity delete(UUID primaryKey) {
		return super.delete(primaryKey);
	}
}
