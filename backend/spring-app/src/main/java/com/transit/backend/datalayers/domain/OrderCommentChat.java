package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderCommentChat")
@Table(name = "orderCommentChat")
@Audited(withModifiedFlag = true)
public class OrderCommentChat extends AbstractEntity implements Serializable, Comparable<OrderCommentChat> {
	
	
	@Type(type = "text")
	private String comment;
	
	private UUID companyId;
	
	private UUID orderId;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean postParent;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean postChild;
	
	@Override
	public int compareTo(@NotNull OrderCommentChat o) {
		if (this.getCreateDate().equals(o.getCreateDate())) {
			return o.getId().compareTo(this.getId());
		}
		return o.getCreateDate().compareTo(this.getCreateDate());
	}
}


