package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "SharingDefaultDTO", itemRelation = "SharingDefaultDTO")
public class SharingDefaultDTO {
	
	private String entity;
	
	private List<SharingDefaultPropertyDTO> companyProperties;
}
