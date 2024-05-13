package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.OrderSuborderController;
import com.transit.backend.datalayers.controller.assembler.OrderSuborderAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderSuborderAssemblerWrapper extends AssemblerWrapperAbstract<Order, OrderDTO, Order, OrderDTO, OrderController, OrderSuborderController> {
	
	@Autowired
	OrderSuborderAssembler orderSuborderAssembler;
	
	public OrderDTO toModel(Order entity, UUID orderId, boolean backwardLink) {
		return super.toModel(entity, orderId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Order, OrderDTO> getAssemblerSupport() {
		return this.orderSuborderAssembler;
	}
	
	public OrderDTO addLinks(OrderDTO dto, UUID orderId, boolean backwardLink) {
		return dto;
	}
	
	@Override
	public Class<OrderSuborderController> getNestedControllerClazz() {
		return OrderSuborderController.class;
	}
	
	@Override
	public Class<OrderController> getRootControllerClazz() {
		return OrderController.class;
	}
	
	@Override
	public Class<Order> getDomainNameClazz() {
		return Order.class;
	}
	
	public CollectionModel<OrderDTO> toCollectionModel(Iterable<? extends Order> entities, UUID orderId, boolean backwardLink) {
		return super.toCollectionModelWithExtraParameter(entities, orderId, backwardLink);
	}
}

