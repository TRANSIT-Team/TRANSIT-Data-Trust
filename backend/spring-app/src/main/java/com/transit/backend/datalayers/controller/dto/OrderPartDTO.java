package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderPartDTO extends AbstractDTO<OrderPartDTO> {
	
	private UUID shortId;
	
	private IdentifierDTO companyId;
	
	private String orderStatus;
	
	private IdentifierDTO contactPerson;
	
	private boolean suborderType;
	
	private double orderAltPrice;
}
