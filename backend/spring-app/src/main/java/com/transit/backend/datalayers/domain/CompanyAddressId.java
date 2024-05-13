package com.transit.backend.datalayers.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Audited(withModifiedFlag = true)
public class CompanyAddressId implements Serializable, Comparable<CompanyAddressId> {
	@Column(name = "companyId")
	private UUID companyId;
	
	@Column(name = "addressId")
	private UUID addressId;
	
	@Override
	public int hashCode() {
		return Objects.hash(companyId, addressId);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof CompanyAddressId that)) return false;
		return companyId.equals(that.companyId) && addressId.equals(that.addressId);
	}
	
	@Override
	public String toString() {
		return "(" + companyId + "," + addressId + ')';
	}
	
	@Override
	public int compareTo(@NotNull CompanyAddressId o) {
		var x = this.companyId.compareTo(o.getCompanyId());
		var y = this.addressId.compareTo(o.getAddressId());
		
		return 2 ^ x * 3 ^ y;
	}
}
