package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntityRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "DefaultSharingRights")
@Table(name = "defaultSharingRights")
@Audited(withModifiedFlag = true)
@Cache(region = "DefaultSharingRightsCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DefaultSharingRights extends AbstractEntityRelation implements Serializable {
	
	//is CompanyId
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;
	
	@Column(name = "defaultSharingRights")
	@Type(type = "text")
	private String defaultSharingRights;
	
}

