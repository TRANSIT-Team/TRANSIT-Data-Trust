package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.CarCarPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CarPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cars/{carId}/carproperties")
public class CarCarPropertyController extends CrudControllerSubRessourceAbstract<CarProperty, CarPropertyDTO, Car, CarDTO, CarController, CarCarPropertyController> implements CrudControllerSubResource<CarPropertyDTO, UUID, UUID> {
	@Autowired
	private CarCarPropertyService carPropertyService;
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	@Autowired
	private CarCarPropertyAssemblerWrapper carCarPropertyAssemblerWrapper;
	@Autowired
	private CarPropertyFilter carPropertyFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> create(@PathVariable UUID carId, @RequestBody @Validated(ValidationGroups.Post.class) CarPropertyDTO dto) {
		return super.create(carId, dto);
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getMapper() {
		return this.carPropertyMapper;
	}
	
	@Override
	public CrudServiceSubRessource<CarProperty, UUID, UUID> getService() {
		return this.carPropertyService;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CarProperty, CarPropertyDTO, Car, CarDTO, CarController, CarCarPropertyController> getAssemblerWrapper() {
		return this.carCarPropertyAssemblerWrapper;
	}
	
	@PutMapping("/{carPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> update(
			@PathVariable UUID carId, @PathVariable UUID carPropertyId, @RequestBody @Validated(ValidationGroups.Put.class) CarPropertyDTO dto) throws ClassNotFoundException {
		return super.update(carId, carPropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<CarProperty, ?> getFilterHelper() {
		return this.carPropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{carPropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> partialUpdate(@PathVariable UUID carId, @PathVariable UUID carPropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(carId, carPropertyId, patch);
	}
	
	@GetMapping("/{carPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CarPropertyDTO> readOne(@PathVariable UUID carId, @PathVariable UUID carPropertyId) {
		return super.readOne(carId, carPropertyId);
	}
	
	@DeleteMapping("/{carPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity delete(@PathVariable UUID carId, @PathVariable UUID carPropertyId) {
		return super.delete(carId, carPropertyId);
	}
	
	@GetMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue)"
	)
	public ResponseEntity<CollectionModel<CarPropertyDTO>> read(@PathVariable UUID carId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                            @RequestParam(name = "take", defaultValue = "0") int take,
	                                                            @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                            @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(carId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
}