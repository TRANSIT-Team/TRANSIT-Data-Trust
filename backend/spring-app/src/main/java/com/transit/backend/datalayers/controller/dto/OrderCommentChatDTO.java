package com.transit.backend.datalayers.controller.dto;

import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.datalayers.controller.dto.enums.ChatIsMy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Column;
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
@Relation(collectionRelation = "ordercommentchatentries", itemRelation = "ordercommentchatentry")
public class OrderCommentChatDTO extends AbstractDTO<OrderCommentChatDTO> {
	
	private String comment;
	
	private UUID companyId;
	
	private UUID orderId;
	
	private boolean postParent;
	
	private boolean postChild;
	
	@Null
	private ChatIsMy person;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderCommentChatDTO that)) return false;
		if (!super.equals(o)) return false;
		return postParent == that.postParent && postChild == that.postChild && Objects.equals(comment, that.comment) && Objects.equals(companyId, that.companyId) && Objects.equals(orderId, that.orderId) && person == that.person;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), comment, companyId, orderId, postParent, postChild, person);
	}
}
