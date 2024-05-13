package com.transit.backend.datalayers.controller.dto;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "userIds", itemRelation = "userId")
public class UserIdDTO extends RepresentationModel<UserIdDTO> {
	
	private UUID userId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		UserIdDTO userIdDTO = (UserIdDTO) o;
		return Objects.equal(userId, userIdDTO.userId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), userId);
	}
}
