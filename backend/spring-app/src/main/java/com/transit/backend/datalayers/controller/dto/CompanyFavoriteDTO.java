package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companyFavorites", itemRelation = "companyFavorite")
public class CompanyFavoriteDTO extends AbstractDTO<CompanyFavoriteDTO> {
	private String name;
	
	private Set<UUID> companyIds;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyFavoriteDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(name, that.name) && Objects.equals(companyIds, that.companyIds);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, companyIds);
	}
}
