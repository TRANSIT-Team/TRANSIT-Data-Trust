package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "ordersWeekly", itemRelation = "orderWeekly")
public class OrdersDaysDTO extends RepresentationModel<OrdersDaysDTO> {
	
	private Date orderDate;
	private long ordersOpen = 0;
	private long ordersProgress = 0 ;
	private long ordersCanceled = 0;
	private long ordersCreated = 0;
	private long ordersComplete = 0;
}


