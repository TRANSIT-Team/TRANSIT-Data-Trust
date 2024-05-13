package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderSuborderAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.OrderSuborderService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.OrderFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders/{orderId}/suborders")
public class OrderSuborderController extends CrudControllerSubRessourceNToMAbstract<Order, OrderDTO, Order, OrderDTO, OrderController, OrderSuborderController> implements CrudControllerSubResource<OrderDTO, UUID, UUID> {
	@Autowired
	private OrderSuborderService service;
	@Autowired
	private OrderMapper mapper;
	@Autowired
	private OrderSuborderAssemblerWrapper orderSuborderAssemblerWrapper;
	@Autowired
	private OrderFilter orderFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> create(@PathVariable UUID orderId, @RequestBody @Validated(ValidationGroups.Post.class) OrderDTO dto) {
		return super.create(orderId, dto);
	}
	
	@Override
	public AbstractMapper<Order, OrderDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<Order, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperAbstract<Order, OrderDTO, Order, OrderDTO, OrderController, OrderSuborderController> getAssemblerWrapper() {
		return this.orderSuborderAssemblerWrapper;
	}
	
	@PutMapping("/{suborderId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> update(
			@PathVariable UUID orderId, @PathVariable UUID suborderId, @RequestBody @Validated(ValidationGroups.Put.class) OrderDTO dto) throws ClassNotFoundException {
		return super.update(orderId, suborderId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Order, ?> getFilterHelper() {
		return this.orderFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{suborderId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> partialUpdate(@PathVariable UUID orderId, @PathVariable UUID suborderId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(orderId, suborderId, patch);
	}
	
	@Override
	public Class<Order> getCLazz() {
		return Order.class;
	}
	
	@GetMapping("/{suborderId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> readOne(@PathVariable UUID orderId, @PathVariable UUID suborderId) {
		return super.readOne(orderId, suborderId);
	}
	
	@DeleteMapping("/{suborderId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID orderId, @PathVariable UUID suborderId) {
		return super.delete(orderId, suborderId);
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
	public ResponseEntity<CollectionModel<OrderDTO>> read(@PathVariable UUID orderId, @RequestParam(name = "filter", defaultValue = "deleted==false;orderProperties.deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                      @RequestParam(name = "take", defaultValue = "0") int take,
	                                                      @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                      @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(orderId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
