package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.util.UUID;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orderLegs", itemRelation = "orderLeg")
public class OrderLegDTO extends AbstractDTO<OrderLegDTO> {
	@NotBlank
	private String type;
	@NotBlank
	private String status;
	private UUID carId;
	private UUID warehouseId;
	private UUID orderRouteId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderLegDTO that = (OrderLegDTO) o;
		return Objects.equal(type, that.type) && Objects.equal(status, that.status) && Objects.equal(carId, that.carId) && Objects.equal(warehouseId, that.warehouseId) && Objects.equal(orderRouteId, that.orderRouteId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), type, status, carId, warehouseId, orderRouteId);
	}
}
