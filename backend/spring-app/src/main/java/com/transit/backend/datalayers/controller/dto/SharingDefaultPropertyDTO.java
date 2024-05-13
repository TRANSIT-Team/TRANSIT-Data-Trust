package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "SharingDefaultPropertyDTO", itemRelation = "SharingDefaultPropertyDTO")
public class SharingDefaultPropertyDTO {
	private String property;
	private boolean read;
	private boolean write;
}
