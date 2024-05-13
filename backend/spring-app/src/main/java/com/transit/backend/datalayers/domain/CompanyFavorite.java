package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CompanyFavorite")
@Table(name = "companyFavorite")
@Audited(withModifiedFlag = true)
@Cache(region = "companyFavoriteCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CompanyFavorite extends AbstractEntity implements Serializable, Comparable<CompanyFavorite> {
	@Type(type = "text")
	private String name;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UUID> companyList;
	
	private UUID companyId;
	
	@Override
	public int compareTo(@NotNull CompanyFavorite o) {
		return this.getId().compareTo(o.getId());
	}
}
