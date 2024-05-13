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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderRoute")
@Table(name = "orderRoute")
@Audited(withModifiedFlag = true)
@Cache(region = "orderRouteCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class OrderRoute extends AbstractEntity implements Serializable {
	
	
	@OneToMany
	
	private List<OrderLeg> orderLegs = new LinkedList<>();
	
	
}
