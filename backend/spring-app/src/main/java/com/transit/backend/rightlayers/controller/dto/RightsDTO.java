package com.transit.backend.rightlayers.controller.dto;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.IdentifierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "rights", itemRelation = "right")
public class RightsDTO {
	String typeClazz;
	IdentifierCompanyDTO creatorCompany;
	Set<CompanyPairsDTO> canReadCompanies;
	Set<CompanyPairsDTO> canWriteCompanies;
	IdentifierDTO authenticateUserId;
	IdentifierCompanyDTO userCompanyId;
	private UUID entityId;
	private boolean deleted;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(entityId, deleted, typeClazz, creatorCompany, canReadCompanies, canWriteCompanies, authenticateUserId, userCompanyId);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RightsDTO rightsDTO)) return false;
		return deleted == rightsDTO.deleted && Objects.equal(entityId, rightsDTO.entityId) && Objects.equal(typeClazz, rightsDTO.typeClazz) && Objects.equal(creatorCompany, rightsDTO.creatorCompany) && Objects.equal(canReadCompanies, rightsDTO.canReadCompanies) && Objects.equal(canWriteCompanies, rightsDTO.canWriteCompanies) && Objects.equal(authenticateUserId, rightsDTO.authenticateUserId) && Objects.equal(userCompanyId, rightsDTO.userCompanyId);
	}
}
