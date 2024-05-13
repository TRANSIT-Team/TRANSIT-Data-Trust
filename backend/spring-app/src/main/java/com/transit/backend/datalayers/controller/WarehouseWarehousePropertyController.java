package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehousePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.WarehouseWarehousePropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.WarehousePropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/warehouses/{warehouseId}/warehouseproperties")
public class WarehouseWarehousePropertyController extends CrudControllerSubRessourceAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseWarehousePropertyController> implements CrudControllerSubResource<WarehousePropertyDTO, UUID, UUID> {
	@Autowired
	private WarehouseWarehousePropertyService warehouseWarehousePropertyService;
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	@Autowired
	private WarehouseWarehousePropertyAssemblerWrapper warehouseWarehousePropertyAssemblerWrapper;
	@Autowired
	private WarehousePropertyFilter warehousePropertyFilter;
	
	@PostMapping
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> create(@PathVariable UUID warehouseId, @RequestBody @Validated(ValidationGroups.Post.class) WarehousePropertyDTO dto) {
		return super.create(warehouseId, dto);
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getMapper() {
		return this.warehousePropertyMapper;
	}
	
	@Override
	public CrudServiceSubRessource<WarehouseProperty, UUID, UUID> getService() {
		return this.warehouseWarehousePropertyService;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseWarehousePropertyController> getAssemblerWrapper() {
		return this.warehouseWarehousePropertyAssemblerWrapper;
	}
	
	@PutMapping("/{warehousePropertyId}")
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> update(
			@PathVariable UUID warehouseId, @PathVariable UUID warehousePropertyId, @RequestBody @Validated(ValidationGroups.Put.class) WarehousePropertyDTO dto) throws ClassNotFoundException {
		return super.update(warehouseId, warehousePropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<WarehouseProperty, ?> getFilterHelper() {
		return this.warehousePropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{warehousePropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> partialUpdate(@PathVariable UUID warehouseId, @PathVariable UUID warehousePropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(warehouseId, warehousePropertyId, patch);
	}
	
	@GetMapping("/{warehousePropertyId}")
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<WarehousePropertyDTO> readOne(@PathVariable UUID warehouseId, @PathVariable UUID warehousePropertyId) {
		return super.readOne(warehouseId, warehousePropertyId);
	}
	
	@DeleteMapping("/{warehousePropertyId}")
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity delete(@PathVariable UUID warehouseId, @PathVariable UUID warehousePropertyId) {
		return super.delete(warehouseId, warehousePropertyId);
	}
	
	@GetMapping
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CollectionModel<WarehousePropertyDTO>> read(@PathVariable UUID warehouseId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                  @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                  @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                  @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(warehouseId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
}
