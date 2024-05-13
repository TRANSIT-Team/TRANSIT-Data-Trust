package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
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
public class IdentifierDTO extends RepresentationModel<IdentifierDTO> implements Serializable {
	
	@NotNull
	UUID id;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IdentifierDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(id, that.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), id);
	}
}
