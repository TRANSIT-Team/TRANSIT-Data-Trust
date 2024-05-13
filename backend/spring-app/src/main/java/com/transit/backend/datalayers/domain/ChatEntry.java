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
@Entity(name = "ChatEntry")
@Table(name = "chatEntry")
@Audited(withModifiedFlag = true)
public class ChatEntry extends AbstractEntity implements Serializable, Comparable<ChatEntry> {
	
	
	private Long sequenceId;
	
	@Type(type = "text")
	private String text;
	
	private UUID orderId;
	
	private UUID companyId;
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean readStatus;
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean emailSendAlready;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ChatEntry chatEntry)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(sequenceId, chatEntry.sequenceId) && Objects.equals(text, chatEntry.text);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), sequenceId, text);
	}
	
	@Override
	public int compareTo(@NotNull ChatEntry o) {
		return (int) (Math.pow(2, this.getId().compareTo(o.getId())) * Math.pow(3, this.sequenceId.compareTo(o.sequenceId)));
	}
}


