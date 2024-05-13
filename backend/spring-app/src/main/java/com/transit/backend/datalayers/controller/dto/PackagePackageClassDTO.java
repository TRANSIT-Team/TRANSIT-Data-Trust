package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdAlwaysNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "packageClasses", itemRelation = "packageClass")
public class PackagePackageClassDTO extends AbstractDTOIdAlwaysNotNull<PackagePackageClassDTO> {
	private String name;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PackagePackageClassDTO)) return false;
		if (!super.equals(o)) return false;
		PackagePackageClassDTO that = (PackagePackageClassDTO) o;
		return Objects.equal(name, that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), name);
	}
}