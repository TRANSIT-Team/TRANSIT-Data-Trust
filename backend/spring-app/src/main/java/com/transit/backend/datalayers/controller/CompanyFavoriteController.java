package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;

import com.transit.backend.datalayers.controller.assembler.CompanyFavoriteOverviewAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyFavoriteAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyFavorite;

import com.transit.backend.datalayers.service.CompanyFavoriteService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyFavoriteMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;

import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.security.filterresponse.implementations.CompanyFavoriteFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/favorites")
public class CompanyFavoriteController extends CrudControllerSubRessourceNToMAbstract<CompanyFavorite, CompanyFavoriteDTO, Company, CompanyDTO, CompanyController, CompanyFavoriteController> implements CrudControllerSubResource<CompanyFavoriteDTO, UUID, UUID> {
	
	@Autowired
	private CompanyFavoriteService service;
	@Autowired
	private CompanyFavoriteMapper mapper;
	@Autowired
	private CompanyFavoriteAssemblerWrapper companyCompanyFavoriteAssemblerWrapper;
	@Autowired
	private CompanyFavoriteFilter companyFavoriteFilter;
	
	@Autowired
	private PingService pingService;
	
	
	@Autowired
	private CompanyFavoriteOverviewAssembler companyFavoriteOverviewAssembler;
	
	
	@PostMapping
	@PreAuthorize(
			"@securityCompanyServiceOneLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyFavoriteDTO> create(@PathVariable UUID companyId, @RequestBody @Validated(ValidationGroups.Post.class) CompanyFavoriteDTO dto) {
		return super.create(companyId, dto);
	}
	
	@Override
	public AbstractMapper<CompanyFavorite, CompanyFavoriteDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<CompanyFavorite, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperAbstract<CompanyFavorite, CompanyFavoriteDTO, Company, CompanyDTO, CompanyController, CompanyFavoriteController> getAssemblerWrapper() {
		return this.companyCompanyFavoriteAssemblerWrapper;
	}
	
	@PutMapping("/{companyFavoriteId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyFavoriteDTO> update(
			@PathVariable UUID companyId, @PathVariable UUID companyFavoriteId, @RequestBody @Validated(ValidationGroups.Put.class) CompanyFavoriteDTO dto) throws ClassNotFoundException {
		return super.update(companyId, companyFavoriteId, dto);
	}
	
	@Override
	public boolean getFilter() {
		//Filtered by Query in Service
		return false;
	}
	
	@Override
	public EntityFilterHelper<CompanyFavorite, ?> getFilterHelper() {
		return this.companyFavoriteFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{companyFavoriteId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyFavoriteDTO> partialUpdate(@PathVariable UUID companyId, @PathVariable UUID companyFavoriteId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(companyId, companyFavoriteId, patch);
	}
	
	@Override
	public Class<CompanyFavorite> getCLazz() {
		return CompanyFavorite.class;
	}
	
	@GetMapping("/{companyFavoriteId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CompanyFavoriteDTO> readOne(@PathVariable UUID companyId, @PathVariable UUID companyFavoriteId) {
		return super.readOne(companyId, companyFavoriteId);
	}
	
	@DeleteMapping("/{companyFavoriteId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID companyId, @PathVariable UUID companyFavoriteId) {
		return super.delete(companyId, companyFavoriteId);
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
	public ResponseEntity<CollectionModel<CompanyFavoriteDTO>> read(@PathVariable UUID companyId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(companyId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
	@GetMapping("/name")
	@PreAuthorize(
			"@securityTwoLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<CompanyFavoriteOverviewDTO>> readName(@PathVariable("companyId") UUID companyId) {
		pingService.available();
		var test = service.getOverview(companyId);
		return new ResponseEntity<>(companyFavoriteOverviewAssembler.toCollectionModel(test), HttpStatus.OK);
		
	}
	
	
}
