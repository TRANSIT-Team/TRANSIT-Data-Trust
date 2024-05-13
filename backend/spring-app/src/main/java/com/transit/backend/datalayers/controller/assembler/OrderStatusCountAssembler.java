package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.DashboardController;
import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import com.transit.backend.datalayers.service.mapper.OrderStatusCountsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderStatusCountAssembler extends RepresentationModelAssemblerSupport<OrderStatusCountsDTO, OrderStatusCountsDTO> {
	
	@Autowired
	private OrderStatusCountsMapper mapper;
	
	public OrderStatusCountAssembler() {
		super(DashboardController.class, OrderStatusCountsDTO.class);
	}
	
	@Override
	public OrderStatusCountsDTO toModel(OrderStatusCountsDTO entity) {
		return mapper
				.toDto(entity)
				.add(linkTo(methodOn(DashboardController.class).readOrderStatus(true, true)).withSelfRel());
	}
}
