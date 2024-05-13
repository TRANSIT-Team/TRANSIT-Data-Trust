package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "customers", itemRelation = "customer")
public class CustomerDTO extends AbstractDTO<CustomerDTO> {
	
	private String name;
	private String email;
	private String tel;
	private UUID companyId;
	
	private UUID addressId;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CustomerDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(tel, that.tel) && Objects.equals(companyId, that.companyId) && Objects.equals(addressId, that.addressId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, email, tel, companyId, addressId);
	}
}
