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
@Relation(collectionRelation = "payments", itemRelation = "payment")
public class PaymentDTO extends AbstractPropertiesParentDTO<PaymentDTO, PaymentPropertyDTO> {
	
	private String paymentStatus;
	
	private Double amount;
	
	@JsonProperty("paymentProperties")
	@Valid
	private SortedSet<PaymentPropertyDTO> paymentProperties;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PaymentDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(paymentStatus, that.paymentStatus) && Objects.equals(paymentProperties, that.paymentProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), paymentStatus, paymentProperties);
	}
	
	@Override
	public SortedSet<PaymentPropertyDTO> getProperties() {
		return this.paymentProperties;
	}
	
	@Override
	public void setProperties(SortedSet<PaymentPropertyDTO> properties) {
		this.paymentProperties = properties;
	}
}
