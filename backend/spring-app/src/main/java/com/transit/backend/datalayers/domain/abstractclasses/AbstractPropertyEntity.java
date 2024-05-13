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
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
@Audited(withModifiedFlag = true)
public abstract class AbstractPropertyEntity<parentEntity extends AbstractParentEntity<?>> extends AbstractEntity implements Serializable {
	
	
	public abstract UUID getParentId();
	
	
	public abstract parentEntity getParent();
	
	public abstract void setParent(parentEntity parent);
	
	public abstract String getKey();
	
	public abstract void setKey(String key);
	
	public abstract String getValue();
	
	public abstract void setValue(String value);
	
	public abstract String getType();
	
	public abstract void setType(String type);
	
	
}
