package com.transit.backend.datalayers.controller.dto.abstractclasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractDTO<P extends AbstractDTO<P>> extends BaseTypeDTO<P> implements Serializable {
	
	@Null(groups = ValidationGroups.Post.class)
	//@Null(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	private UUID id;
	
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
	
	@AssertFalse(groups = ValidationGroups.Post.class)
	@AssertFalse(groups = ValidationGroups.Put.class)
	@AssertFalse(groups = ValidationGroups.Patch.class)
	private boolean deleted;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractDTO<?> that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(id, that.id) && Objects.equals(createDate, that.createDate) && Objects.equals(modifyDate, that.modifyDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, createDate, modifyDate);
	}
}
