package com.transit.backend.rightlayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.IdentifierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "identifiers", itemRelation = "identifier")
public class IdentifierCompanyDTO extends RepresentationModel<IdentifierDTO> implements Serializable {
	
	@NotNull
	UUID companyId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IdentifierCompanyDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(companyId, that.companyId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyId);
	}
}
