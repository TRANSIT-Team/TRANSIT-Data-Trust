package com.transit.backend.rightlayers.controller.dto;

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
@Relation(collectionRelation = "entityProperties", itemRelation = "entityProperty")
public class EntityPropertyDTO {
	
	private UUID id;
	
	private String name;
	
	private String entityClazz;
	
}
