package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GlobalCompanyProperties")
@Table(name = "globalcompanyproperties")
@Audited(withModifiedFlag = true)
@Cache(region = "globalCompanyPropertiesCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class GlobalCompanyProperties extends AbstractEntity {
	private String name;
	private String type;
}
