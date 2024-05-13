package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehouseAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.service.WarehouseAddressService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.AddressFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/warehouses/{warehouseId}/warehouseaddresses")
public class WarehouseAddressController extends CrudControllerSubRessourceNToMAbstract<Address, AddressDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseAddressController> implements CrudControllerSubResource<AddressDTO, UUID, UUID> {
	@Autowired
	private WarehouseAddressService warehouseAddressService;
	@Autowired
	private AddressMapper locationMapper;
	@Autowired
	private WarehouseWarehouseAddressAssemblerWrapper warehouseWarehouseAddressAssemblerWrapper;
	@Autowired
	private AddressFilter addressFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<AddressDTO> create(@PathVariable UUID warehouseId, @RequestBody @Validated(ValidationGroups.Post.class) AddressDTO dto) {
		return super.create(warehouseId, dto);
	}
	
	@Override
	public AbstractMapper<Address, AddressDTO> getMapper() {
		return this.locationMapper;
	}
	
	@Override
	public CrudServiceSubRessource<Address, UUID, UUID> getService() {
		return this.warehouseAddressService;
	}
	
	@Override
	public AssemblerWrapperAbstract<Address, AddressDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseAddressController> getAssemblerWrapper() {
		return this.warehouseWarehouseAddressAssemblerWrapper;
	}
	
	@PutMapping("/{warehouseAddressId}")
	@PreAuthorize(
			"hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<AddressDTO> update(
			@PathVariable UUID warehouseId, @PathVariable UUID warehouseAddressId, @RequestBody @Validated(ValidationGroups.Put.class) AddressDTO dto) throws ClassNotFoundException {
		return super.update(warehouseId, warehouseAddressId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Address, ?> getFilterHelper() {
		return this.addressFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{warehouseAddressId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<AddressDTO> partialUpdate(@PathVariable UUID warehouseId, @PathVariable UUID warehouseAddressId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(warehouseId, warehouseAddressId, patch);
	}
	
	@Override
	public Class<Address> getCLazz() {
		return Address.class;
	}
	
	@GetMapping("/{warehouseAddressId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<AddressDTO> readOne(@PathVariable UUID warehouseId, @PathVariable UUID warehouseAddressId) {
		return super.readOne(warehouseId, warehouseAddressId);
	}
	
	@DeleteMapping("/{warehouseAddressId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity delete(@PathVariable UUID warehouseId, @PathVariable UUID warehouseAddressId) {
		return super.delete(warehouseId, warehouseAddressId);
	}
	
	@GetMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CollectionModel<AddressDTO>> read(@PathVariable UUID warehouseId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                        @RequestParam(name = "take", defaultValue = "0") int take,
	                                                        @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                        @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(warehouseId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
}
