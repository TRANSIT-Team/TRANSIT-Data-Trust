package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.PackageItemPackagePackagePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.PackageItemPackagePackagePropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.PackagePackagePropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/packageitems/{packageItemId}/packagepackageproperties")
public class PackageItemPackagePackagePropertyController extends CrudControllerSubRessourceAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackageItemPackagePackagePropertyController> implements CrudControllerSubResource<PackagePackagePropertyDTO, UUID, UUID> {
	@Autowired
	private PackageItemPackagePackagePropertyService service;
	@Autowired
	private PackagePackagePropertyMapper mapper;
	@Autowired
	private PackageItemPackagePackagePropertyAssemblerWrapper packagePackagePropertyAssembler;
	@Autowired
	private PackagePackagePropertyFilter packagePackagePropertyFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> create(@PathVariable UUID packageItemId, @RequestBody @Validated(ValidationGroups.Post.class) PackagePackagePropertyDTO dto) {
		return super.create(packageItemId, dto);
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<PackagePackageProperty, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackageItemPackagePackagePropertyController> getAssemblerWrapper() {
		return this.packagePackagePropertyAssembler;
	}
	
	@PutMapping("/{packagePropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> update(
			@PathVariable UUID packageItemId, @PathVariable UUID packagePropertyId, @RequestBody @Validated(ValidationGroups.Put.class) PackagePackagePropertyDTO dto) throws ClassNotFoundException {
		return super.update(packageItemId, packagePropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<PackagePackageProperty, ?> getFilterHelper() {
		return this.packagePackagePropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{packagePropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PackagePackagePropertyDTO> partialUpdate(@PathVariable UUID packageItemId, @PathVariable UUID packagePropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(packageItemId, packagePropertyId, patch);
	}
	
	@GetMapping("/{packagePropertyId}")
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
	public ResponseEntity<PackagePackagePropertyDTO> readOne(@PathVariable UUID packageItemId, @PathVariable UUID packagePropertyId) {
		return super.readOne(packageItemId, packagePropertyId);
	}
	
	@DeleteMapping("/{packagePropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority()"
	)
	public ResponseEntity delete(@PathVariable UUID packageItemId, @PathVariable UUID packagePropertyId) {
		return super.delete(packageItemId, packagePropertyId);
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
	public ResponseEntity<CollectionModel<PackagePackagePropertyDTO>> read(@PathVariable UUID packageItemId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                       @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                       @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                       @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(packageItemId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
