package com.transit.backend.datalayers.domain.abstractclasses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;
import java.util.SortedSet;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
@Audited(withModifiedFlag = true)
public abstract class AbstractParentEntity<property extends AbstractPropertyEntity<?>> extends AbstractEntity implements Serializable {
	
	
	public abstract SortedSet<property> getProperties();
	
	public abstract void setProperties(SortedSet<property> properties);
	
	public abstract void addProperty(property property);
	
	
}
