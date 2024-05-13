package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.BaseTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orderStatus", itemRelation = "orderStatus")
public class OrderStatusDTO extends BaseTypeDTO<OrderStatusDTO> implements Serializable {
	@NotBlank
	private String orderStatus;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderStatusDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(orderStatus, that.orderStatus);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), orderStatus);
	}
}
