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
@Relation(collectionRelation = "packageClasses", itemRelation = "packageClass")
public class PackageClassDTO extends AbstractDTO<PackageClassDTO> {
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String name;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PackageClassDTO)) return false;
		if (!super.equals(o)) return false;
		PackageClassDTO that = (PackageClassDTO) o;
		return name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name);
	}
}
