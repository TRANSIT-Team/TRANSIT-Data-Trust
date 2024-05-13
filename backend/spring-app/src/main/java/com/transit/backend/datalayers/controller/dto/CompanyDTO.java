package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.SortedSet;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companies", itemRelation = "company")
public class CompanyDTO extends AbstractPropertiesParentDTO<CompanyDTO, CompanyPropertyDTO> {
	//@Valid
	//@JsonIgnore
	//@Null(groups = ValidationGroups.Post.class)
	//@Null(groups = ValidationGroups.Put.class)
	//@NotNull(groups = ValidationGroups.Patch.class)
	//private Set<CompanyAddressDTO> companyAddresses;
	//@Valid
	//@JsonIgnore
	//@Null(groups = ValidationGroups.Post.class)
	//@Null(groups = ValidationGroups.Put.class)
	//@NotNull(groups = ValidationGroups.Patch.class)
	//private Set<UserDTO> companyUsers;
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String name;
	
	@Valid
	private SortedSet<CompanyPropertyDTO> companyProperties;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(companyProperties, that.companyProperties) && Objects.equal(name, that.name); // Objects.equal(companyAddresses, that.companyAddresses) && Objects.equal(companyUsers, that.companyUsers) &&
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyProperties, name); //companyAddresses, companyUsers,
	}
	
	@Override
	public SortedSet<CompanyPropertyDTO> getProperties() {
		return this.companyProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CompanyPropertyDTO> properties) {
		this.setCompanyProperties(properties);
		
	}
}
