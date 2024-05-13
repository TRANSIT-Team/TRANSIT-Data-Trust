package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.datalayers.controller.dto.enums.ChatIsMy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "chatentries", itemRelation = "chatentry")
public class ChatEntryDTO extends AbstractDTO<ChatEntryDTO> {
	@Null
	private Long sequenceId;
	@NotBlank
	private String text;
	@NotNull
	private UUID orderId;
	@Null
	
	private UUID companyId;
	
	@AssertFalse
	private boolean readStatus;
	
	@Null
	private ChatIsMy person;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ChatEntryDTO that)) return false;
		if (!super.equals(o)) return false;
		return readStatus == that.readStatus && Objects.equals(sequenceId, that.sequenceId) && Objects.equals(text, that.text) && Objects.equals(orderId, that.orderId) && Objects.equals(companyId, that.companyId) && person == that.person;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), sequenceId, text, orderId, companyId, readStatus, person);
	}
}
