package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "locations", itemRelation = "location")
public class LocationDTO extends AbstractDTO<LocationDTO> implements Comparable<LocationDTO> {
	@NotNull(groups = ValidationGroups.Post.class)
	@NotNull(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	private Point locationPoint;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LocationDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(locationPoint, that.locationPoint);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), locationPoint);
	}
	
	@Override
	public int compareTo(@org.jetbrains.annotations.NotNull LocationDTO o) {
		if (this.locationPoint == null || o.getLocationPoint() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.locationPoint.compareTo(o.getLocationPoint());
	}
}
