package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Null;
import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "contactPersons", itemRelation = "contactPerson")
public class ContactPersonDTO extends AbstractDTO<ContactPersonDTO> {
	private String name;
	
	private String email;
	
	private String phone;
	
	@Null(groups = ValidationGroups.Post.class)
	
	private UUID companyId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContactPersonDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, email, phone);
	}
}
