package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.OrderOrderPropertyController;
import com.transit.backend.datalayers.controller.assembler.OrderPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderOrderPropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<OrderProperty, OrderPropertyDTO, Order, OrderDTO, OrderController, OrderOrderPropertyController> {
	
	@Autowired
	OrderPropertyAssembler orderPropertyAssembler;
	
	public OrderPropertyDTO toModel(OrderProperty entity, UUID packageItemId, boolean backwardLink) {
		return super.toModel(entity, packageItemId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<OrderProperty, OrderPropertyDTO> getAssemblerSupport() {
		return this.orderPropertyAssembler;
	}
	
	public OrderPropertyDTO addLinks(OrderPropertyDTO dto, UUID packageItemId, boolean backwardLink) {
		return super.addLinks(dto, packageItemId, backwardLink);
	}
	
	@Override
	public Class<OrderOrderPropertyController> getNestedControllerClazz() {
		return OrderOrderPropertyController.class;
	}
	
	@Override
	public Class<OrderController> getRootControllerClazz() {
		return OrderController.class;
	}
	
	@Override
	public Class<Order> getDomainNameClazz() {
		return Order.class;
	}
	
	public CollectionModel<OrderPropertyDTO> toCollectionModel(Iterable<? extends OrderProperty> entities, UUID packageItemId, boolean backwardLink) {
		return super.toCollectionModel(entities, packageItemId, backwardLink);
	}
}
