package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.OrderOrderPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class OrderPropertyAssembler extends AbstractAssemblerWithoutLink<OrderProperty, OrderPropertyDTO, OrderOrderPropertyController> {
	@Autowired
	private OrderPropertyMapper packagePackagePropertyMapper;
	
	
	public OrderPropertyAssembler() {
		super(OrderOrderPropertyController.class, OrderPropertyDTO.class);
	}
	
	@Override
	public OrderPropertyDTO toModel(OrderProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<OrderProperty, OrderPropertyDTO> getMapper() {
		return this.packagePackagePropertyMapper;
	}
	
	@Override
	public CollectionModel<OrderPropertyDTO> toCollectionModel(Iterable<? extends OrderProperty> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}
