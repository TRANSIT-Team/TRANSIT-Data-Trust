package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.*;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderOrderPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.OrderOverviewDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.mapper.OrderOverviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderOverviewAssembler extends RepresentationModelAssemblerSupport<Order, OrderOverviewDTO> {
	
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private OrderOverviewMapper orderFullMapper;
	@Autowired
	private OrderOrderPropertyAssemblerWrapper orderOrderPropertyAssembler;
	
	
	public OrderOverviewAssembler() {
		super(OrderController.class, OrderOverviewDTO.class);
	}
	
	@Override
	public OrderOverviewDTO toModel(Order order) {
		OrderOverviewDTO dto = orderFullMapper.toDto(order);
		dto.add(linkTo(methodOn(OrderController.class).readOne(order.getId())).withSelfRel());
		dto.add(linkTo(methodOn(OrderController.class).readOneFull(dto.getId())).withRel("fullOrder"));
		dto.add(linkTo(methodOn(OrderController.class).readOnePart(dto.getId())).withRel("partOrder"));
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
	
	
	public OrderOverviewDTO addLinks(OrderOverviewDTO orderDTO) {
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
