package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyDeliveryAreaAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.service.CompanyDeliveryAreaService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyDeliveryAreaMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CompanyDeliveryAreaFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/deliveryarea")
public class CompanyDeliveryAreaController extends CrudControllerSubRessourceNToMAbstract<CompanyDeliveryArea, CompanyDeliveryAreaDTO, Company, CompanyDTO, CompanyController, CompanyDeliveryAreaController> implements CrudControllerSubResource<CompanyDeliveryAreaDTO, UUID, UUID> {
	@Autowired
	private CompanyDeliveryAreaService companyDeliveryAreaService;
	@Autowired
	private CompanyCompanyDeliveryAreaAssemblerWrapper companyDeliveryAreaAssembler;
	@Autowired
	private CompanyDeliveryAreaMapper companyDeliveryAreaMapper;
	@Autowired
	private CompanyDeliveryAreaFilter companyDeliveryAreaFilter;
	
	@Override
	@PostMapping
	@PreAuthorize(
			"@securityCompanyServiceOneLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDeliveryAreaDTO> create(@PathVariable UUID companyId, @RequestBody @Validated(ValidationGroups.Post.class) CompanyDeliveryAreaDTO dto) {
		return super.create(companyId, dto);
	}
	
	@Override
	public AbstractMapper<CompanyDeliveryArea, CompanyDeliveryAreaDTO> getMapper() {
		return this.companyDeliveryAreaMapper;
	}
	
	@Override
	public CrudServiceSubRessource<CompanyDeliveryArea, UUID, UUID> getService() {
		return this.companyDeliveryAreaService;
	}
	
	@Override
	public AssemblerWrapperAbstract<CompanyDeliveryArea, CompanyDeliveryAreaDTO, Company, CompanyDTO, CompanyController, CompanyDeliveryAreaController> getAssemblerWrapper() {
		return this.companyDeliveryAreaAssembler;
	}
	
	@Override
	@PutMapping("/{companyDeliveryAreaId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDeliveryAreaDTO> update(
			@PathVariable UUID companyId, @PathVariable UUID companyDeliveryAreaId, @RequestBody @Validated(ValidationGroups.Put.class) CompanyDeliveryAreaDTO dto) throws ClassNotFoundException {
		return super.update(companyId, companyDeliveryAreaId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<CompanyDeliveryArea, ?> getFilterHelper() {
		return this.companyDeliveryAreaFilter;
	}
	
	@Override
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{companyDeliveryAreaId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDeliveryAreaDTO> partialUpdate(@PathVariable UUID companyId, @PathVariable UUID companyDeliveryAreaId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(companyId, companyDeliveryAreaId, patch);
	}
	
	@Override
	public Class<CompanyDeliveryArea> getCLazz() {
		return CompanyDeliveryArea.class;
	}
	
	@Override
	@GetMapping("/{companyDeliveryAreaId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CompanyDeliveryAreaDTO> readOne(@PathVariable UUID companyId, @PathVariable UUID companyDeliveryAreaId) {
		return super.readOne(companyId, companyDeliveryAreaId);
	}
	
	@Override
	@DeleteMapping("/{companyDeliveryAreaId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID companyId, @PathVariable UUID companyDeliveryAreaId) {
		return super.delete(companyId, companyDeliveryAreaId);
	}
	
	@Override
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
	public ResponseEntity<CollectionModel<CompanyDeliveryAreaDTO>> read(@PathVariable UUID companyId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                    @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                    @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                    @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(companyId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
}
