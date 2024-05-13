package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.CompanyAssembler;
import com.transit.backend.datalayers.controller.assembler.CompanyOutsourceAssembler;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.DefaultSharingRightsDTO;
import com.transit.backend.datalayers.controller.dto.outsource.CompanyOutsourceDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.DefaultSharingRights;
import com.transit.backend.datalayers.service.CompanyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CompanyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/companies")
@Slf4j
public class CompanyController extends CrudControllerExtendAbstract<Company, UUID, CompanyDTO> implements CrudControllerExtend<CompanyDTO, UUID> {
	
	@Autowired
	private CompanyFilter companyFilter;
	@Autowired
	private CompanyService service;
	@Autowired
	private CompanyMapper mapper;
	@Autowired
	private CompanyAssembler companyAssembler;
	@Autowired
	private PagedResourcesAssembler<Company> pagedResourcesAssembler;
	@Autowired
	private CompanyOutsourceAssembler companyOutsourceAssembler;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	@PreAuthorize("hasAnyAuthority()")
	public ResponseEntity<CompanyDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) CompanyDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<Company, CompanyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<Company, UUID> getService() {
		return this.service;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Company, CompanyDTO> getAssemblerSupport() {
		return this.companyAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) CompanyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<Company, Company> getFilterHelper() {
		return this.companyFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<CompanyDTO> getClazz() {
		return CompanyDTO.class;
	}
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		
		var response = super.readOne(primaryKey);
		
		return response;
	}
	
	@Override
	@PreAuthorize(
			"@securityCompanyService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable("id") UUID primaryKey) {
		return super.delete(primaryKey);
	}
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<PagedModel<CompanyDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;companyProperties.deleted==false") String query,
	                                                   @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                   @RequestParam(name = "take", defaultValue = "0") int take,
	                                                   @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
		
	}
	
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@GetMapping(path = "/overview")
	public ResponseEntity<PagedModel<CompanyOutsourceDTO>> read_Full(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;companyProperties.deleted==false") String query,
	                                                                 @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                 @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                 @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		var page = service.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query);
		PagedModel<CompanyOutsourceDTO> pages;
		if (page.hasContent()) {
			pages = pagedResourcesAssembler.toModel(page, companyOutsourceAssembler);
		} else {
			pages = (PagedModel<CompanyOutsourceDTO>) pagedResourcesAssembler.toEmptyModel(page, CompanyOutsourceDTO.class);
		}
		return new ResponseEntity<>(pages, HttpStatus.OK);
		
	}
	
	@PreAuthorize(
			"@securityDefaultSharingRightsService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	@GetMapping("/{id}/defaultsharingrights")
	public ResponseEntity getDefaultSharingRights(@PathVariable("id") UUID primaryKey) throws JsonProcessingException {
		var nodeStringOpt = service.getDefaultSharingRights(primaryKey);
		if (nodeStringOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(new DefaultSharingRightsDTO(objectMapper.readTree(nodeStringOpt.get().getDefaultSharingRights())), HttpStatus.OK);
		}
		
	}
	
	@PreAuthorize(
			"@securityDefaultSharingRightsService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	@PostMapping("/{id}/defaultsharingrights")
	public ResponseEntity setDefaultSharingRights(@PathVariable("id") UUID primaryKey, @RequestBody DefaultSharingRightsDTO dto) throws JsonProcessingException {
		var defaultSharingRights = new DefaultSharingRights();
		defaultSharingRights.setId(primaryKey);
		defaultSharingRights.setDefaultSharingRights(dto.getDefaultSharingRights().toPrettyString());
		return new ResponseEntity<>(new DefaultSharingRightsDTO(objectMapper.readTree(service.setDefaultSharingRights(defaultSharingRights).getDefaultSharingRights())), HttpStatus.OK);
	}
}