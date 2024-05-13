package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.DashboardController;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.OrdersDaysDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderDaysAssembler extends RepresentationModelAssemblerSupport<OrdersDaysDTO, OrdersDaysDTO> {
	
	public OrderDaysAssembler() {
		super(DashboardController.class, OrdersDaysDTO.class);
	}
	
	@Override
	public OrdersDaysDTO toModel(OrdersDaysDTO entity) {
		return entity.add(linkTo(methodOn(DashboardController.class).getDailyOrders(7)).withSelfRel());
	}
}
