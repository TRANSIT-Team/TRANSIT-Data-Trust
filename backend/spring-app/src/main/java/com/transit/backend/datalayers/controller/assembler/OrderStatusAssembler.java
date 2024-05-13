package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.dto.OrderStatusDTO;
import com.transit.backend.transferentities.OrderStatusTransferObject;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderStatusAssembler extends RepresentationModelAssemblerSupport<OrderStatusTransferObject, OrderStatusDTO> {
	
	public OrderStatusAssembler() {
		super(OrderController.class, OrderStatusDTO.class);
	}
	
	@Override
	public OrderStatusDTO toModel(OrderStatusTransferObject entity) {
		var dto = new OrderStatusDTO(entity.getOrderStatus());
		dto.add(linkTo(methodOn(OrderController.class).readOne(entity.getOrderId())).withRel("order"));
		dto.add(linkTo(methodOn(OrderController.class).readOrderStatus(entity.getOrderId())).withSelfRel());
		
		return dto;
	}
}
