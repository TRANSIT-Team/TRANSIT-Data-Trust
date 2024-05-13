package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orderStatusCounts", itemRelation = "orderStatusCount")
public class OrderStatusCountDTO {
	
	
	private long ACCEPTED;
	private long OPEN;
	private long PROCESSING;
	private long COMPLETE;
	private long CANCELED;
	private long REJECTED;
	private long REQUESTED;
	private long REVOKED;
	private long CREATED;
	
}
