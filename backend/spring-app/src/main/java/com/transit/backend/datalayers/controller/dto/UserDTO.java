package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDTO extends AbstractPropertiesParentDTO<UserDTO, UserPropertyDTO> {
	@NotNull(groups = ValidationGroups.Post.class)
	@NotNull(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	private UUID companyId;
	
	//@NotNull(groups = ValidationGroups.Post.class)
	//@Null(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	private UUID keycloakId;
	
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String firstName;
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String lastName;
	
	@Email(groups = ValidationGroups.Post.class, message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@NotBlank(groups = ValidationGroups.Post.class, message = "Email cannot be empty")
	@Email(groups = ValidationGroups.Put.class, message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@NotBlank(groups = ValidationGroups.Put.class, message = "Email cannot be empty")
	@Email(groups = ValidationGroups.Patch.class, message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@NotBlank(groups = ValidationGroups.Patch.class, message = "Email cannot be empty")
	private String email;
	
	@AssertTrue
	private boolean enabled;
	
	@NotEmpty
	@NotEmpty
	private List<String> realmRoles;
	
	@Null
	
	private List<String> groups;
	
	@JsonProperty("userProperties")
	@Valid
	private SortedSet<UserPropertyDTO> userProperties;
	
	
	private String jobPosition;
	
	public UserDTO(User user) {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserDTO userDTO)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(companyId, userDTO.companyId) && Objects.equal(firstName, userDTO.firstName) && Objects.equal(lastName, userDTO.lastName) && Objects.equal(email, userDTO.email) && Objects.equal(realmRoles, userDTO.realmRoles) && Objects.equal(groups, userDTO.groups) && Objects.equal(userProperties, userDTO.userProperties) && Objects.equal(jobPosition, userDTO.jobPosition);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyId, firstName, lastName, email, realmRoles, groups, userProperties, jobPosition);
	}
	
	@Override
	public SortedSet<UserPropertyDTO> getProperties() {
		return this.userProperties;
	}
	
	@Override
	public void setProperties(SortedSet<UserPropertyDTO> properties) {
		this.setUserProperties(properties);
	}
}
