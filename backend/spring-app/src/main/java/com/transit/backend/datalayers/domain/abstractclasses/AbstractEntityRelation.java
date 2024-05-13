package com.transit.backend.datalayers.domain.abstractclasses;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor

public class AbstractEntityRelation implements Serializable {
	
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
		return Objects.hashCode(createDate, modifyDate, createdBy, lastModifiedBy, deleted);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof AbstractEntityRelation that)) return false;
		return deleted == that.deleted && Objects.equal(createDate, that.createDate) && Objects.equal(modifyDate, that.modifyDate) && Objects.equal(createdBy, that.createdBy) && Objects.equal(lastModifiedBy, that.lastModifiedBy);
	}
}