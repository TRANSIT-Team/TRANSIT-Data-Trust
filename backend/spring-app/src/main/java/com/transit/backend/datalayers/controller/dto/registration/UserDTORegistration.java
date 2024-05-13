package com.transit.backend.datalayers.controller.dto.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDTORegistration extends AbstractPropertiesParentDTO<UserDTORegistration, UserPropertyDTO> {
	
	
	@NotNull(groups = ValidationGroups.Post.class)
	private UUID keycloakId;
	
	
	@NotEmpty
	private List<String> realmRoles;
	
	@Null
	private List<String> groups;
	
	
	@JsonProperty("userProperties")
	@Valid
	private SortedSet<UserPropertyDTO> userProperties;
	
	
	private String jobPosition;
	@Valid
	@NotNull
	private CompanyDTORegistration company;
	
	@AssertTrue
	private boolean enabled;
	
	public UserDTORegistration() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		UserDTORegistration that = (UserDTORegistration) o;
		return Objects.equal(keycloakId, that.keycloakId) && Objects.equal(realmRoles, that.realmRoles) && Objects.equal(groups, that.groups) && Objects.equal(userProperties, that.userProperties) && Objects.equal(jobPosition, that.jobPosition) && Objects.equal(company, that.company);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), keycloakId, realmRoles, groups, userProperties, jobPosition, company);
	}
	
	@Override
	public SortedSet<UserPropertyDTO> getProperties() {
		return this.userProperties;
	}
	
	@Override
	public void setProperties(SortedSet<UserPropertyDTO> properties) {
		this.userProperties = properties;
	}
}
