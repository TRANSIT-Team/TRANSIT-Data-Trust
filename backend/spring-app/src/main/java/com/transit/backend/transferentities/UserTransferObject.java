package com.transit.backend.transferentities;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;

@Data
@AllArgsConstructor
public class UserTransferObject {
	UserRepresentation userRepresentation;
	User user;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(userRepresentation, user);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserTransferObject that)) return false;
		return Objects.equal(userRepresentation, that.userRepresentation) && Objects.equal(user, that.user);
	}
}
