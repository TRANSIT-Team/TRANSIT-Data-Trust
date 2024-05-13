package com.transit.backend.datalayers.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CompanyIDs")
@Table(name = "companiesids")
@Audited(withModifiedFlag = true)
@Cache(region = "cCompanyIDsCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CompanyIDToCompanyOID {
	
	
	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	private UUID companyOID;
	
	public UUID getCompanyId() {
		return this.id;
	}
	
	public void setCompanyId(UUID companyId) {
		this.id = companyId;
	}
	
}
