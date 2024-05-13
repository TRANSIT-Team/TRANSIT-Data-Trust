package com.transit.backend.datalayers.controller.dto.abstractclasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.SortedSet;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractPropertiesParentDTO<testDTO extends AbstractDTO<testDTO>, propertiesDTO> extends AbstractDTO<testDTO> {
	
	@JsonIgnore
	public abstract SortedSet<propertiesDTO> getProperties();
	
	@JsonIgnore
	
	public abstract void setProperties(SortedSet<propertiesDTO> properties);
	
	
}
