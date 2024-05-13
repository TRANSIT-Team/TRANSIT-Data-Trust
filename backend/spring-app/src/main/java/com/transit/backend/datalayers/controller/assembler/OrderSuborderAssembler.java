package com.transit.backend.datalayers.controller.assembler;


import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.OrderSuborderController;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderSuborderAssembler extends RepresentationModelAssemblerSupport<Order, OrderDTO> {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderAssembler orderAssembler;
	
	
	public OrderSuborderAssembler() {
		super(OrderSuborderController.class, OrderDTO.class);
	}
	
	@Override
	public OrderDTO toModel(Order entity) {
		var dto = orderAssembler.toModel(entity);
		var links = dto.getLinks().stream().filter(link -> !link.hasRel(IanaLinkRelations.SELF)).toList();
		dto.removeLinks();
		for (var link : links) {
			dto.add(link);
		}
		dto.add(linkTo(methodOn(OrderSuborderController.class).readOne(entity.getParentOrder().getId(), entity.getId())).withSelfRel());
		dto.add(linkTo(methodOn(OrderController.class).readOne(entity.getId())).withRel("order"));
		return dto;
	}
	
	@Override
	public CollectionModel<OrderDTO> toCollectionModel(Iterable<? extends Order> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}