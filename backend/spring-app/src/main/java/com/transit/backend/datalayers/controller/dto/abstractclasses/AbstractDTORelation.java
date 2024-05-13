package com.transit.backend.datalayers.controller.dto.abstractclasses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.transit.backend.config.jackson.JacksonOffSerDateTimeMapperSerialize;
import com.transit.backend.config.jackson.JacksonOffsetDateTimeMapper;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AbstractDTORelation<P extends AbstractDTORelation<P>> extends BaseTypeDTO<P> implements Serializable {
	
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime createDate;
	
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime modifyDate;
	
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String createdBy;
	
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	private String lastModifiedBy;
	@AssertFalse
	private boolean deleted;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractDTORelation)) return false;
		if (!super.equals(o)) return false;
		AbstractDTORelation<?> that = (AbstractDTORelation<?>) o;
		return deleted == that.deleted && Objects.equal(createDate, that.createDate) && Objects.equal(modifyDate, that.modifyDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), createDate, modifyDate, deleted);
	}
}
