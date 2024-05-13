package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.service.CompanyCompanyAddressService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CompanyAddressFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/companyaddresses")
public class CompanyCompanyAddressController extends CrudControllerSubRessourceNToMAbstract<CompanyAddress, CompanyAddressDTO, Company, CompanyDTO, CompanyController, CompanyCompanyAddressController> implements CrudControllerSubResource<CompanyAddressDTO, UUID, UUID> {
	
	@Autowired
	private CompanyCompanyAddressService service;
	@Autowired
	private CompanyAddressMapper mapper;
	@Autowired
	private CompanyCompanyAddressAssemblerWrapper companyCompanyAddressAssemblerWrapper;
	@Autowired
	private CompanyAddressFilter companyAddressFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityCompanyServiceOneLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyAddressDTO> create(@PathVariable UUID companyId, @RequestBody @Validated(ValidationGroups.Post.class) CompanyAddressDTO dto) {
		return super.create(companyId, dto);
	}
	
	@Override
	public AbstractMapper<CompanyAddress, CompanyAddressDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<CompanyAddress, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperAbstract<CompanyAddress, CompanyAddressDTO, Company, CompanyDTO, CompanyController, CompanyCompanyAddressController> getAssemblerWrapper() {
		return this.companyCompanyAddressAssemblerWrapper;
	}
	
	@PutMapping("/{companyAddressId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyAddressDTO> update(
			@PathVariable UUID companyId, @PathVariable UUID companyAddressId, @RequestBody @Validated(ValidationGroups.Put.class) CompanyAddressDTO dto) throws ClassNotFoundException {
		return super.update(companyId, companyAddressId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<CompanyAddress, ?> getFilterHelper() {
		return this.companyAddressFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{companyAddressId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyAddressDTO> partialUpdate(@PathVariable UUID companyId, @PathVariable UUID companyAddressId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(companyId, companyAddressId, patch);
	}
	
	@Override
	public Class<CompanyAddress> getCLazz() {
		return CompanyAddress.class;
	}
	
	@GetMapping("/{companyAddressId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CompanyAddressDTO> readOne(@PathVariable UUID companyId, @PathVariable UUID companyAddressId) {
		return super.readOne(companyId, companyAddressId);
	}
	
	@DeleteMapping("/{companyAddressId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID companyId, @PathVariable UUID companyAddressId) {
		return super.delete(companyId, companyAddressId);
	}
	
	@GetMapping
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<CompanyAddressDTO>> read(@PathVariable UUID companyId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                               @RequestParam(name = "take", defaultValue = "0") int take,
	                                                               @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                               @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(companyId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
