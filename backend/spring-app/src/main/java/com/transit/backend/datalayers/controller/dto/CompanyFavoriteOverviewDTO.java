package com.transit.backend.datalayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companyFavoritesOverview", itemRelation = "companyFavoriteOverview")
public class CompanyFavoriteOverviewDTO extends RepresentationModel<CompanyFavoriteOverviewDTO> {
	
	private String name;
	
	private UUID id;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyFavoriteOverviewDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(name, that.name) && Objects.equals(id, that.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, id);
	}
}
