package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.ContactPersonDTO;
import com.transit.backend.datalayers.domain.ContactPerson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactPersonMapper extends AbstractMapper<ContactPerson, ContactPersonDTO> {
	ContactPerson toEntity(ContactPersonDTO dto);
	
	ContactPersonDTO toDto(ContactPerson entity);
	
}
