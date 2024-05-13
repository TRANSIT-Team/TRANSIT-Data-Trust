package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class ContactPersonFilter extends AbstractEntityFilter<ContactPerson, ContactPerson> implements EntityFilterHelper<ContactPerson, ContactPerson> {
	@Override
	public ContactPerson transformToTransfer(ContactPerson entity) {
		return entity;
	}
	
	@Override
	public ContactPerson transformToEntity(ContactPerson entity) {
		return entity;
	}
	
	@Override
	public ContactPerson transformToTransfer(ContactPerson entity, ContactPerson entityOld) {
		return entity;
	}
	
	@Override
	public Class<ContactPerson> getClazz() {
		return ContactPerson.class;
	}
	
	@Override
	public String getPathToEntity(ContactPerson entity, ContactPerson entity2) {
		return "/contactpersons/" + entity.getId();
	}
}
