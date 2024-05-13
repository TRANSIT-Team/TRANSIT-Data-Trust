package com.transit.backend.datalayers.controller.dto.registration;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.SortedSet;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companies", itemRelation = "company")
public class CompanyDTORegistration extends AbstractPropertiesParentDTO<CompanyDTORegistration, CompanyPropertyDTO> {
	@Valid
	@NotEmpty(message = "Company minimal have one Address")
	private Set<CompanyAddressDTORegistration> companyAddresses;
	
	
	@NotBlank(groups = ValidationGroups.Post.class)
	private String name;
	
	@Valid
	private SortedSet<CompanyPropertyDTO> companyProperties;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CompanyDTORegistration that = (CompanyDTORegistration) o;
		return Objects.equal(companyAddresses, that.companyAddresses) && Objects.equal(name, that.name) && Objects.equal(companyProperties, that.companyProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyAddresses, name, companyProperties);
	}
	
	@Override
	public SortedSet<CompanyPropertyDTO> getProperties() {
		return this.companyProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CompanyPropertyDTO> properties) {
		this.companyProperties = properties;
	}
}
