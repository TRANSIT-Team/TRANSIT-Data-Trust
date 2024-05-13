package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "deliveryMethods", itemRelation = "deliveryMethod")
public class DeliveryMethodDTO extends AbstractDTO<DeliveryMethodDTO> {
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String deliveryMethodName;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeliveryMethodDTO)) return false;
		if (!super.equals(o)) return false;
		DeliveryMethodDTO that = (DeliveryMethodDTO) o;
		return Objects.equals(deliveryMethodName, that.deliveryMethodName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), deliveryMethodName);
	}
}
