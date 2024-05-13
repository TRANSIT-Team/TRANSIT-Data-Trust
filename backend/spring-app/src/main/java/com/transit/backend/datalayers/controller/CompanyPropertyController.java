package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerNestedAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.CompanyPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CompanyPropertyFilter;
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
@RequestMapping("/companyproperties")
public class CompanyPropertyController extends CrudControllerNestedAbstract<CompanyProperty, UUID, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyPropertyController> implements CrudControllerNested<CompanyPropertyDTO, UUID> {
	
	
	@Autowired
	private CompanyPropertyService service;
	
	@Autowired
	private CompanyPropertyMapper mapper;
	
	@Autowired
	private CompanyPropertyAssemblerWrapper companyPropertyAssembler;
	@Autowired
	private CompanyPropertyFilter companyPropertyFilter;
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) CompanyPropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceNested<CompanyProperty, UUID> getService() {
		return this.service;
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<CompanyProperty, ?> getFilterHelper() {
		return this.companyPropertyFilter;
	}
	
	@Override
	public AssemblerWrapperNestedAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyPropertyController> getAssemblerWrapper() {
		return this.companyPropertyAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(UUID primaryKey) {
		return super.delete(primaryKey);
	}
}