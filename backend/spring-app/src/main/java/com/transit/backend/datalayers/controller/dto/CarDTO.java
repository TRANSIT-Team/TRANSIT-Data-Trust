package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.SortedSet;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "cars", itemRelation = "car")
public class CarDTO extends AbstractPropertiesParentDTO<CarDTO, CarPropertyDTO> {
	@NotBlank
	private String plate;
	@NotBlank
	private String type;
	private String capacity;
	private String weight;
	
	
	@Valid
	private SortedSet<LocationDTO> locations;
	
	@Valid
	private SortedSet<CarPropertyDTO> carProperties;
	
	@Valid
	private SortedSet<OrderLegDTO> orderLegs;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CarDTO carDTO = (CarDTO) o;
		return Objects.equal(plate, carDTO.plate) && Objects.equal(type, carDTO.type) && Objects.equal(capacity, carDTO.capacity) && Objects.equal(weight, carDTO.weight) && Objects.equal(carProperties, carDTO.carProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), plate, type, capacity, weight, carProperties);
	}
	
	@Override
	public SortedSet<CarPropertyDTO> getProperties() {
		return this.carProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CarPropertyDTO> properties) {
		this.setCarProperties(properties);
	}
}
