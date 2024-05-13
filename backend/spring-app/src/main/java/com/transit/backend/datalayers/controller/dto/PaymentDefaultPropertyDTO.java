package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Relation(collectionRelation = "paymentDefaultProperties", itemRelation = "paymentDefaultProperty")
public class PaymentDefaultPropertyDTO extends AbstractDTO<PaymentDefaultPropertyDTO> {
	
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String key;
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String defaultValue;
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String type;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PaymentDefaultPropertyDTO)) return false;
		if (!super.equals(o)) return false;
		PaymentDefaultPropertyDTO that = (PaymentDefaultPropertyDTO) o;
		return Objects.equal(key, that.key) && Objects.equal(defaultValue, that.defaultValue) && Objects.equal(type, that.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, defaultValue, type);
	}
}
		
