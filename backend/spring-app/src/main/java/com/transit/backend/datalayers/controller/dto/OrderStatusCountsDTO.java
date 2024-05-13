package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orderStatusCountSummary", itemRelation = "orderStatusCountSummary")
public class OrderStatusCountsDTO extends RepresentationModel<OrderStatusCountsDTO> {
	private OrderStatusCountDTO ownOrders;
	
	private OrderStatusCountDTO ownSuborders;
	
	private OrderStatusCountDTO acceptedSubOrdes;
	
	
	private OrderStatus parentOrderStatus;
}
