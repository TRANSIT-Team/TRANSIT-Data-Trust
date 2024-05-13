package com.transit.backend.datalayers.domain.abstractclasses;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
@Audited(withModifiedFlag = true)

public abstract class AbstractEntity implements Serializable {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	
	private UUID id;
	
	
	@CreationTimestamp
	@Column(name = "create_date", nullable = false, updatable = false)
	private OffsetDateTime createDate;
	
	@UpdateTimestamp
	@Column(name = "modify_date", nullable = false)
	private OffsetDateTime modifyDate;
	
	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	@Type(type = "text")
	private String createdBy;
	
	@LastModifiedBy
	@Column(name = "last_modified_by", nullable = false)
	@Type(type = "text")
	private String lastModifiedBy;
	
	
	private boolean deleted;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id, createDate, modifyDate, createdBy, lastModifiedBy, deleted);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractEntity that)) return false;
		return deleted == that.deleted && Objects.equal(id, that.id) && Objects.equal(createDate, that.createDate) && Objects.equal(modifyDate, that.modifyDate) && Objects.equal(createdBy, that.createdBy) && Objects.equal(lastModifiedBy, that.lastModifiedBy);
	}
}
