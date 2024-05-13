package com.transit.backend.datalayers.controller.dto.abstractclasses;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.RepresentationModel;


@AllArgsConstructor
public abstract class BaseTypeDTO<T extends BaseTypeDTO<T>> extends RepresentationModel<T> {
	
	protected BaseTypeDTO(final BaseTypeDTOBuilder<T, ?, ?> b) {
		super();
	}
	
	protected abstract static class BaseTypeDTOBuilder<
			T extends BaseTypeDTO<T>,
			C extends BaseTypeDTO<T>,
			B extends BaseTypeDTOBuilder<T, C, B>
			> {
		
		protected abstract B self();
		
		public abstract C build();
	}
}