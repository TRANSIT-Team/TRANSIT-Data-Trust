package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdCanBeNull;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Relation(collectionRelation = "packagePackageProperties", itemRelation = "packagePackageProperty")
public class PackagePackagePropertyDTO extends AbstractDTOIdCanBeNull<PackagePackagePropertyDTO> implements Comparable<PackagePackagePropertyDTO> {
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String key;
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String value;
	
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String type;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PackagePackagePropertyDTO)) return false;
		if (!super.equals(o)) return false;
		PackagePackagePropertyDTO that = (PackagePackagePropertyDTO) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public int compareTo(@NotNull PackagePackagePropertyDTO o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
}
