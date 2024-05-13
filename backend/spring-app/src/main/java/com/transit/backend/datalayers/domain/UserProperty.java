package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserProperty")
@Table(name = "userProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "userPropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserProperty extends AbstractPropertyEntity<User> implements Serializable, Comparable<UserProperty> {
	@Type(type = "text")
	private String key;
	
	@Type(type = "text")
	private String value;
	@Type(type = "text")
	private String type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	
	private User user;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof UserProperty that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
	
	@Override
	public int compareTo(@NotNull UserProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
	
	@Override
	
	public UUID getParentId() {
		return user.getId();
	}
	
	@Override
	
	public User getParent() {
		return this.user;
	}
	
	@Override
	public void setParent(User parent) {
		this.setUser(parent);
	}
	
	
}
