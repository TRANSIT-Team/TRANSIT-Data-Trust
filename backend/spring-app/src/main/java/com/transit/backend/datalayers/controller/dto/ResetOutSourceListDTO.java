package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "ResetOutSourceListDTO", itemRelation = "ResetOutSourceListDTO")
public class ResetOutSourceListDTO {
	private List<SharingDefaultDTO> defaultSharingRights;
}
