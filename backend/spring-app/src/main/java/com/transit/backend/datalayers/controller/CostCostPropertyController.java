package com.transit.backend.datalayers.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CostCostPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CostDTO;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.service.CostPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CostPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/costs/{costId}/costproperties")
public class CostCostPropertyController extends CrudControllerSubRessourceAbstract<CostProperty, CostPropertyDTO, Cost, CostDTO, CostController, CostCostPropertyController> implements CrudControllerSubResource<CostPropertyDTO, UUID, UUID> {
	@Autowired
	private CostPropertyFilter costPropertyFilter;
	
	@Autowired
	private CostCostPropertyAssemblerWrapper costCostPropertiesAssemblerWrapper;
	
	@Autowired
	private CostPropertyService costCostPropertyService;
	
	@Autowired
	private CostPropertyMapper mapper;
	
	@PostMapping
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					
					")"
	)
	public ResponseEntity<CostPropertyDTO> create(@PathVariable UUID costId, @RequestBody @Validated(ValidationGroups.Post.class) CostPropertyDTO dto) {
		return super.create(costId, dto);
	}
	
	@Override
	public AbstractMapper<CostProperty, CostPropertyDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public CrudServiceSubRessource<CostProperty, UUID, UUID> getService() {
		return costCostPropertyService;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CostProperty, CostPropertyDTO, Cost, CostDTO, CostController, CostCostPropertyController> getAssemblerWrapper() {
		return costCostPropertiesAssemblerWrapper;
	}
	
	@PutMapping("/{costPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<CostPropertyDTO> update(
			@PathVariable UUID costId, @PathVariable UUID costPropertyId, @RequestBody @Validated(ValidationGroups.Put.class) CostPropertyDTO dto) throws ClassNotFoundException {
		return super.update(costId, costPropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<CostProperty, ?> getFilterHelper() {
		return costPropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{costPropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<CostPropertyDTO> partialUpdate(@PathVariable UUID costId, @PathVariable UUID costPropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(costId, costPropertyId, patch);
	}
	
	@GetMapping("/{costPropertyId}")
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
	public ResponseEntity<CostPropertyDTO> readOne(@PathVariable UUID costId, @PathVariable UUID costPropertyId) {
		return super.readOne(costId, costPropertyId);
	}
	
	@DeleteMapping("/{costPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority()"
	)
	public ResponseEntity delete(@PathVariable UUID costId, @PathVariable UUID costPropertyId) {
		return super.delete(costId, costPropertyId);
	}
	
	@GetMapping
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
	public ResponseEntity<CollectionModel<CostPropertyDTO>> read(@PathVariable UUID costId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                             @RequestParam(name = "take", defaultValue = "0") int take,
	                                                             @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                             @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(costId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
}
