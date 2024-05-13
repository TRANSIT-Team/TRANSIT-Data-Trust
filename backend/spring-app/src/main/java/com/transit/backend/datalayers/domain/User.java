package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "users")
@Audited(withModifiedFlag = true)
@Cache(region = "userCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class User extends AbstractParentEntity<UserProperty> implements Serializable, Comparable<User> {
	
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	@OrderBy("key ASC")
	private SortedSet<UserProperty> userProperties;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company;
	
	@Type(type = "text")
	private String jobPosition;
	
	private UUID keycloakId;
	@Type(type = "text")
	private String keycloakEmail;
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof User user)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(userProperties, user.userProperties) && Objects.equal(jobPosition, user.jobPosition) && Objects.equal(keycloakId, user.keycloakId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), userProperties, jobPosition, keycloakId);
	}
	
	@Override
	public String toString() {
		return "User{" +
				"userProperties=" + userProperties +
				", jobPosition='" + jobPosition + '\'' +
				", keycloakId=" + keycloakId +
				'}';
	}
	
	@Override
	public SortedSet<UserProperty> getProperties() {
		return this.userProperties;
	}
	
	@Override
	public void setProperties(SortedSet<UserProperty> userProperties) {
		this.setUserProperties(userProperties);
	}
	
	public void setUserProperties(SortedSet<UserProperty> userProperties) {
		if (this.userProperties == null) {
			this.userProperties = new TreeSet<>();
		}
		if (!this.userProperties.equals(userProperties)) {
			this.userProperties.clear();
			if (userProperties != null) {
				this.userProperties.addAll(userProperties);
			}
		}
	}
	
	@Override
	public void addProperty(UserProperty userProperty) {
		this.addUserProperties(userProperty);
	}
	
	public void addUserProperties(UserProperty userProperty) {
		if (this.userProperties == null) {
			this.userProperties = new TreeSet<>();
		}
		this.userProperties.add(userProperty);
		userProperty.setUser(this);
	}
	
	@Override
	public int compareTo(@NotNull User o) {
		return this.getId().compareTo(o.getId());
	}
	
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (this.userProperties == null) {
			this.userProperties = new TreeSet<>();
		} else {
			this.userProperties.forEach(property -> property.setUser(this));
		}
	}
}
