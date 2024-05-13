package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.ContactPersonController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.ContactPersonDTO;
import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.ContactPersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class ContactPersonAssembler extends AbstractAssembler<ContactPerson, ContactPersonDTO, ContactPersonController> {
	
	@Autowired
	private ContactPersonMapper contactPersonMapper;
	
	public ContactPersonAssembler() {
		super(ContactPersonController.class, ContactPersonDTO.class);
	}
	
	@Override
	public ContactPersonDTO toModel(ContactPerson entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<ContactPerson, ContactPersonDTO> getMapper() {
		return this.contactPersonMapper;
	}
	
	@Override
	public CollectionModel<ContactPersonDTO> toCollectionModel(Iterable<? extends ContactPerson> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<ContactPersonController> getControllerClass() {
		return ContactPersonController.class;
	}
	
	
}
	

