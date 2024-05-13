package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.CompanyCompanyPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CompanyPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/companyproperties")
public class CompanyCompanyPropertyController extends CrudControllerSubRessourceAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyCompanyPropertyController> implements CrudControllerSubResource<CompanyPropertyDTO, UUID, UUID> {
	@Autowired
	private CompanyCompanyPropertyService service;
	@Autowired
	private CompanyPropertyMapper mapper;
	@Autowired
	private CompanyCompanyPropertyAssemblerWrapper companyCompanyPropertyAssemblerWrapper;
	@Autowired
	private CompanyPropertyFilter companyPropertyFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityCompanyServiceOneLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> create(@PathVariable UUID companyId, @RequestBody @Validated(ValidationGroups.Post.class) CompanyPropertyDTO dto) {
		return super.create(companyId, dto);
		
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<CompanyProperty, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyCompanyPropertyController> getAssemblerWrapper() {
		return this.companyCompanyPropertyAssemblerWrapper;
	}
	
	@PutMapping("/{companyPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> update(
			@PathVariable UUID companyId, @PathVariable UUID companyPropertyId, @RequestBody @Validated(ValidationGroups.Put.class) CompanyPropertyDTO dto) throws ClassNotFoundException {
		return super.update(companyId, companyPropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<CompanyProperty, ?> getFilterHelper() {
		return this.companyPropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{companyPropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> partialUpdate(@PathVariable UUID companyId, @PathVariable UUID companyPropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(companyId, companyPropertyId, patch);
	}
	
	@GetMapping("/{companyPropertyId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyPropertyDTO> readOne(@PathVariable UUID companyId, @PathVariable UUID companyPropertyId) {
		return super.readOne(companyId, companyPropertyId);
	}
	
	@DeleteMapping("/{companyPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID companyId, @PathVariable UUID companyPropertyId) {
		return super.delete(companyId, companyPropertyId);
	}
	
	@GetMapping
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<CompanyPropertyDTO>> read(@PathVariable UUID companyId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(companyId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
