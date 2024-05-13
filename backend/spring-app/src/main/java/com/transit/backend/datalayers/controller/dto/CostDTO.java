package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import java.util.Objects;
import java.util.SortedSet;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "costs", itemRelation = "cost")
public class CostDTO extends AbstractPropertiesParentDTO<CostDTO, CostPropertyDTO> {
	
	
	@JsonProperty("costProperties")
	@Valid
	private SortedSet<CostPropertyDTO> costProperties;
	
	private double costSum;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CostDTO costDTO)) return false;
		if (!super.equals(o)) return false;
		return Double.compare(costSum, costDTO.costSum) == 0 && Objects.equals(costProperties, costDTO.costProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), costProperties, costSum);
	}
	
	@Override
	public SortedSet<CostPropertyDTO> getProperties() {
		return this.costProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CostPropertyDTO> properties) {
		this.costProperties = properties;
	}
}
