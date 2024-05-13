package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.*;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.helper.SetSuborderOrderFields;
import com.transit.backend.datalayers.controller.assembler.wrapper.OrderOrderPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler extends AbstractAssemblerExtendProperties<Order, OrderDTO, OrderProperty, OrderPropertyDTO, OrderOrderPropertyController, OrderController> {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private OrderMapper packageItemMapper;
	@Autowired
	private OrderOrderPropertyAssemblerWrapper orderOrderPropertyAssembler;
	
	@Autowired
	private SetSuborderOrderFields setSuborderOrderFields;
	
	
	public OrderAssembler() {
		super(OrderController.class, OrderDTO.class);
	}
	
	@Override
	public OrderDTO toModel(Order order) {
		
		var dto = super.toModel(order);
		dto.add(linkTo(methodOn(OrderController.class).readOneFull(dto.getId())).withRel("fullOrder"));
		dto.add(linkTo(methodOn(OrderController.class).readOnePart(dto.getId())).withRel("partOrder"));
		dto.add(linkTo(methodOn(OrderOrderPropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("orderProperties"));
		if (dto.getAddressIdFrom() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressIdFrom().getId())).withRel("addressFrom"));
		}
		if (dto.getAddressIdTo() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressIdTo().getId())).withRel("addressTo"));
		}
		if (dto.getAddressIdBilling() != null) {
			dto.add(linkTo(methodOn(AddressController.class).readOne(dto.getAddressIdBilling().getId())).withRel("addressBilling"));
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
		
		dto = setSuborderOrderFields.setSuborderOrderSettings(dto, order);
		
		dto = addLinks(dto);
		return dto;
	}
	
	@Override
	public AbstractMapper<Order, OrderDTO> getMapper() {
		return this.packageItemMapper;
	}
	
	@Override
	public Class<OrderController> getControllerClass() {
		return OrderController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<OrderProperty, OrderPropertyDTO, Order, OrderDTO, OrderController, OrderOrderPropertyController> getSubAssemblerWrapper() {
		return this.orderOrderPropertyAssembler;
	}
	
	public OrderDTO addLinks(OrderDTO orderDTO) {
		if (orderDTO.getAddressIdFrom() != null) {
			var from = orderDTO.getAddressIdFrom();
			from.add(linkTo(methodOn(AddressController.class).readOne(from.getId())).withSelfRel());
			orderDTO.setAddressIdFrom(from);
		}
		if (orderDTO.getAddressIdTo() != null) {
			var to = orderDTO.getAddressIdTo();
			to.add(linkTo(methodOn(AddressController.class).readOne(to.getId())).withSelfRel());
			orderDTO.setAddressIdTo(to);
		}
		if (orderDTO.getAddressIdBilling() != null) {
			var billing = orderDTO.getAddressIdBilling();
			billing.add(linkTo(methodOn(AddressController.class).readOne(billing.getId())).withSelfRel());
			orderDTO.setAddressIdBilling(billing);
		}
		if (orderDTO.getCompanyId() != null) {
			var company = orderDTO.getCompanyId();
			company.add(linkTo(methodOn(CompanyController.class).readOne(company.getId())).withSelfRel());
			orderDTO.setCompanyId(company);
		}
		if (orderDTO.getDeliveryMethodId() != null) {
			throw new NotImplementedException();
		}
		
		if (orderDTO.getSuborderIds() != null && !orderDTO.getSuborderIds().isEmpty()) {
			orderDTO.setSuborderIds(
					orderDTO.getSuborderIds().stream().map(pI -> {
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
		
		if (orderDTO.getPackageItemIds() != null && !orderDTO.getPackageItemIds().isEmpty()) {
			orderDTO.setPackageItemIds(
					orderDTO.getPackageItemIds().stream().map(pI -> {
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
