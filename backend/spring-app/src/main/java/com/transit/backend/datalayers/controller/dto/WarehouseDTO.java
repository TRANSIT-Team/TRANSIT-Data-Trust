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
@Relation(collectionRelation = "warehouses", itemRelation = "warehouse")
public class WarehouseDTO extends AbstractPropertiesParentDTO<WarehouseDTO, WarehousePropertyDTO> {
	
	private AddressDTO address;
	@Valid
	private SortedSet<WarehousePropertyDTO> warehouseProperties;
	private Long capacity;
	@NotBlank
	private String name;
	@Valid
	private SortedSet<OrderLegDTO> orderLegs;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		WarehouseDTO that = (WarehouseDTO) o;
		return Objects.equal(address, that.address) && Objects.equal(warehouseProperties, that.warehouseProperties) && Objects.equal(capacity, that.capacity);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), address, warehouseProperties, capacity);
	}
	
	@Override
	public SortedSet<WarehousePropertyDTO> getProperties() {
		return this.warehouseProperties;
	}
	
	@Override
	public void setProperties(SortedSet<WarehousePropertyDTO> properties) {
		this.setWarehouseProperties(properties);
	}
}
