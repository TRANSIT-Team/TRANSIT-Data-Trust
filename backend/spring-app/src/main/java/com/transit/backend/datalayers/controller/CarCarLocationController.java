package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubExtraAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarLocationAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubExtraAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.CarLocationService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.LocationFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cars/{carId}/carlocations")
public class CarCarLocationController extends CrudControllerSubExtraAbstract<Location, LocationDTO, Car, CarDTO, CarController, CarCarLocationController> implements CrudControllerSubResource<LocationDTO, UUID, UUID> {
	
	@Autowired
	private CarLocationService carLocationService;
	@Autowired
	private LocationMapper locationMapper;
	@Autowired
	private CarCarLocationAssemblerWrapper carCarLocationAssemblerWrapper;
	@Autowired
	private LocationFilter locationFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<LocationDTO> create(@PathVariable UUID carId, @RequestBody @Validated(ValidationGroups.Post.class) LocationDTO dto) {
		return super.create(carId, dto);
	}
	
	@Override
	public AbstractMapper<Location, LocationDTO> getMapper() {
		return this.locationMapper;
	}
	
	@Override
	public CrudServiceSubRessource<Location, UUID, UUID> getService() {
		return this.carLocationService;
	}
	
	@Override
	public AssemblerWrapperSubExtraAbstract<Location, LocationDTO, Car, CarDTO, CarController, CarCarLocationController> getAssemblerWrapper() {
		return this.carCarLocationAssemblerWrapper;
	}
	
	@PutMapping("/{carLocationId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<LocationDTO> update(
			@PathVariable UUID carId, @PathVariable UUID carLocationId, @RequestBody @Validated(ValidationGroups.Put.class) LocationDTO dto) throws ClassNotFoundException {
		return super.update(carId, carLocationId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Location, ?> getFilterHelper() {
		return this.locationFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{carLocationId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<LocationDTO> partialUpdate(@PathVariable UUID carId, @PathVariable UUID carLocationId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(carId, carLocationId, patch);
	}
	
	@GetMapping("/{carLocationId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<LocationDTO> readOne(@PathVariable UUID carId, @PathVariable UUID carLocationId) {
		return super.readOne(carId, carLocationId);
	}
	
	@DeleteMapping("/{carLocationId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity delete(@PathVariable UUID carId, @PathVariable UUID carLocationId) {
		return super.delete(carId, carLocationId);
	}
	
	@GetMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CollectionModel<LocationDTO>> read(@PathVariable UUID carId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                         @RequestParam(name = "take", defaultValue = "0") int take,
	                                                         @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                         @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(carId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
