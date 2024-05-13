package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Geometry;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companyDeliveryAreas", itemRelation = "companyDeliveryArea")
public class CompanyDeliveryAreaDTO extends AbstractDTO<CompanyDeliveryAreaDTO> {
	@JsonProperty("companyId")
	@NotNull(groups = ValidationGroups.Post.class)
	@NotNull(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	private UUID companyId;
	
	@NotEmpty
	@NotNull
	private List<String> deliveryAreaZips;
	
	private String deliveryAreaPolyline;
	
	
	private Geometry deliveryAreaGeom;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyDeliveryAreaDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(companyId, that.companyId) && Objects.equal(deliveryAreaZips, that.deliveryAreaZips) && Objects.equal(deliveryAreaPolyline, that.deliveryAreaPolyline) && Objects.equal(deliveryAreaGeom, that.deliveryAreaGeom);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyId, deliveryAreaZips, deliveryAreaPolyline, deliveryAreaGeom);
	}
}
