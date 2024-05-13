package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.*;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderOrderPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.OrderFullDTO;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.service.mapper.OrderFullMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderFullAssembler extends RepresentationModelAssemblerSupport<Order, OrderFullDTO> {
	
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private OrderFullMapper orderFullMapper;
	@Autowired
	private OrderOrderPropertyAssemblerWrapper orderOrderPropertyAssembler;
	
	
	public OrderFullAssembler() {
		super(OrderController.class, OrderFullDTO.class);
	}
	
	@Override
	public OrderFullDTO toModel(Order order) {
		OrderFullDTO dto = orderFullMapper.toDto(order);
		dto.add(linkTo(methodOn(OrderController.class).readOneFull(order.getId())).withSelfRel());
		dto.add(linkTo(methodOn(OrderController.class).readOne(dto.getId())).withRel("order"));
		dto.add(linkTo(methodOn(OrderController.class).readOnePart(dto.getId())).withRel("partOrder"));
		
		dto.setProperties(toPropertyDTO(order.getId(), order.getProperties()));
		dto.add(linkTo(methodOn(OrderOrderPropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("orderProperties"));
		if (dto.getAddressFrom() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressFrom().getId())).withRel("addressFrom"));
		}
		if (dto.getAddressTo() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressTo().getId())).withRel("addressTo"));
		}
		if (dto.getAddressBilling() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressBilling().getId())).withRel("addressBilling"));
		}
		if (dto.getCompanyId() != null) {
			dto.add(linkTo(methodOn(CompanyController.class).readOne(dto.getCompanyId().getId())).withRel("company"));
		}
		
		if (dto.getDeliveryMethodId() != null) {
			throw new NotImplementedException();
		}
		if (dto.getParentOrderId() != null) {
			dto.add(linkTo(methodOn(OrderController.class).readOne(dto.getParentOrderId().getId())).withRel("parentOrder"));
		}
		
		dto.add(linkTo(methodOn(OrderController.class).readOrderStatus(dto.getId())).withRel("orderStatus"));
		
		if (dto.getOldOrderId() != null) {
			dto.add(linkTo(methodOn(OrderController.class).readOne(dto.getOldOrderId())).withRel("oldOrder"));
		}
		
		
		if (dto.getNewOrderId() != null) {
			dto.add(linkTo(methodOn(OrderController.class).readOne(dto.getNewOrderId())).withRel("newOrder"));
		}
		
		if (dto.getCustomerId() != null) {
			dto.add(linkTo(methodOn(CompanyCustomerController.class).readOne(dto.getId(), dto.getCustomerId().getId())).withRel("customer"));
		}
		
		dto = addLinks(dto);
		return dto;
	}
	
	private SortedSet<OrderPropertyDTO> toPropertyDTO(UUID rootId, SortedSet<OrderProperty> properties) {
		if (properties == null || properties.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(orderOrderPropertyAssembler.toCollectionModel(properties, rootId, false).getContent());
		}
	}
	
	public OrderFullDTO addLinks(OrderFullDTO orderDTO) {
		if (orderDTO.getAddressFrom() != null) {
			var from = orderDTO.getAddressFrom();
			from.add(linkTo(methodOn(AddressController.class).readOne(from.getId())).withSelfRel());
			orderDTO.setAddressFrom(from);
		}
		if (orderDTO.getAddressTo() != null) {
			var to = orderDTO.getAddressTo();
			to.add(linkTo(methodOn(AddressController.class).readOne(to.getId())).withSelfRel());
			orderDTO.setAddressTo(to);
		}
		if (orderDTO.getAddressBilling() != null) {
			var billing = orderDTO.getAddressBilling();
			billing.add(linkTo(methodOn(AddressController.class).readOne(billing.getId())).withSelfRel());
			orderDTO.setAddressBilling(billing);
		}
		if (orderDTO.getCompanyId() != null) {
			var company = orderDTO.getCompanyId();
			company.add(linkTo(methodOn(CompanyController.class).readOne(company.getId())).withSelfRel());
			orderDTO.setCompanyId(company);
		}
		if (orderDTO.getDeliveryMethodId() != null) {
			throw new NotImplementedException();
		}
		
		if (orderDTO.getSuborders() != null && !orderDTO.getSuborders().isEmpty()) {
			orderDTO.setSuborders(
					orderDTO.getSuborders().stream().map(pI -> {
						pI.add(linkTo(methodOn(OrderSuborderController.class).readOne(orderDTO.getId(), pI.getId())).withSelfRel());
						pI.add(linkTo(methodOn(OrderController.class).readOne(pI.getId())).withRel("order"));
						
						return pI;
					}).toList());
		}
		if (orderDTO.getOrderTypeIds() != null && !orderDTO.getOrderTypeIds().isEmpty()) {
			throw new NotImplementedException();
		}
		if (orderDTO.getOrderRouteIds() != null && !orderDTO.getOrderRouteIds().isEmpty()) {
			throw new NotImplementedException();
		}
		if (orderDTO.getPaymentId() != null) {
			var from = orderDTO.getPaymentId();
			from.add(linkTo(methodOn(PaymentController.class).readOne(from.getId())).withSelfRel());
			orderDTO.setPaymentId(from);
		}
		if (orderDTO.getCostId() != null) {
			var from = orderDTO.getCostId();
			from.add(linkTo(methodOn(CostController.class).readOne(from.getId())).withSelfRel());
			orderDTO.setCostId(from);
		}
		
		if (orderDTO.getPackageItems() != null && !orderDTO.getPackageItems().isEmpty()) {
			orderDTO.setPackageItems(
					orderDTO.getPackageItems().stream().map(pI -> {
						pI.add(linkTo(methodOn(PackageItemController.class).readOne(pI.getId())).withSelfRel());
						return pI;
					}).collect(Collectors.toSet()));
		}
		
		if (orderDTO.getParentOrderId() != null) {
			var parentOrder = orderDTO.getParentOrderId();
			parentOrder.add(linkTo(methodOn(OrderController.class).readOne(parentOrder.getId())).withSelfRel());
			orderDTO.setParentOrderId(parentOrder);
		}
		
		
		return orderDTO;
	}
	
}
