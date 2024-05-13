package com.transit.backend.datalayers.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderOrderPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.service.OrderOrderPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.OrderPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders/{orderId}/orderproperties")
public class OrderOrderPropertyController extends CrudControllerSubRessourceAbstract<OrderProperty, OrderPropertyDTO, Order, OrderDTO, OrderController, OrderOrderPropertyController> implements CrudControllerSubResource<OrderPropertyDTO, UUID, UUID> {
	
	@Autowired
	private OrderOrderPropertyService service;
	@Autowired
	private OrderPropertyMapper mapper;
	@Autowired
	private OrderOrderPropertyAssemblerWrapper orderOrderPropertyAssembler;
	@Autowired
	private OrderPropertyFilter orderPropertyFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityParentEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderPropertyDTO> create(@PathVariable UUID orderId, @RequestBody @Validated(ValidationGroups.Post.class) OrderPropertyDTO dto) {
		return super.create(orderId, dto);
	}
	
	@Override
	public AbstractMapper<OrderProperty, OrderPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<OrderProperty, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<OrderProperty, OrderPropertyDTO, Order, OrderDTO, OrderController, OrderOrderPropertyController> getAssemblerWrapper() {
		return this.orderOrderPropertyAssembler;
	}
	
	@PutMapping("/{orderPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderPropertyDTO> update(
			@PathVariable UUID orderId, @PathVariable UUID orderPropertyId, @RequestBody @Validated(ValidationGroups.Put.class) OrderPropertyDTO dto) throws ClassNotFoundException {
		return super.update(orderId, orderPropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<OrderProperty, ?> getFilterHelper() {
		return this.orderPropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{orderPropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderPropertyDTO> partialUpdate(@PathVariable UUID orderId, @PathVariable UUID orderPropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(orderId, orderPropertyId, patch);
	}
	
	@GetMapping("/{orderPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderPropertyDTO> readOne(@PathVariable UUID orderId, @PathVariable UUID orderPropertyId) {
		return super.readOne(orderId, orderPropertyId);
	}
	
	@DeleteMapping("/{orderPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID orderId, @PathVariable UUID orderPropertyId) {
		return super.delete(orderId, orderPropertyId);
	}
	
	@GetMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<OrderPropertyDTO>> read(@PathVariable UUID orderId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                              @RequestParam(name = "take", defaultValue = "0") int take,
	                                                              @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                              @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(orderId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
