package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.dto.OrderPartDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.mapper.OrderPartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderPartAssembler extends RepresentationModelAssemblerSupport<Order, OrderPartDTO> {
	
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private OrderPartMapper orderFullMapper;
	
	
	public OrderPartAssembler() {
		super(OrderController.class, OrderPartDTO.class);
	}
	
	@Override
	public OrderPartDTO toModel(Order order) {
		OrderPartDTO dto = orderFullMapper.toDto(order);
		dto.add(linkTo(methodOn(OrderController.class).readOneFull(order.getId())).withSelfRel());
		dto.add(linkTo(methodOn(OrderController.class).readOne(dto.getId())).withRel("order"));
		dto.add(linkTo(methodOn(OrderController.class).readOneFull(dto.getId())).withRel("fullOrder"));
		
		
		dto.add(linkTo(methodOn(OrderController.class).readOrderStatus(dto.getId())).withRel("orderStatus"));
		
		return dto;
	}
	
	
}
