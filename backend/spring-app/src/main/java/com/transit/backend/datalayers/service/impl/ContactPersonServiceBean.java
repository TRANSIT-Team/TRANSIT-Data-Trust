package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.ContactPersonDTO;
import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.datalayers.domain.QContactPerson;
import com.transit.backend.datalayers.repository.ContactPersonRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.ContactPersonService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.ContactPersonMapper;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ContactPersonServiceBean extends CrudServiceExtendAbstract<ContactPerson, ContactPersonDTO> implements ContactPersonService {
	
	@Autowired
	private ContactPersonRepository contactPersonRepository;
	@Autowired
	private ContactPersonMapper mapper;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Override
	public AbstractRepository<ContactPerson> getRepository() {
		return this.contactPersonRepository;
	}
	
	@Override
	public AbstractMapper<ContactPerson, ContactPersonDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public Class<ContactPerson> getEntityClazz() {
		return ContactPerson.class;
	}
	
	@Override
	public Class<ContactPersonDTO> getEntityDTOClazz() {
		return ContactPersonDTO.class;
	}
	
	@Override
	public Path<ContactPerson> getQClazz() {
		return QContactPerson.contactPerson;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public ContactPerson create(ContactPerson entity) {
		entity = super.createInternal(entity);
		entity.setCompanyId(userHelperFunctions.getCompanyId());
		return super.saveInternal(entity);
	}
	
	@Override
	public ContactPerson update(UUID primaryKey, ContactPerson entity) {
		var oldEntity = contactPersonRepository.findById(primaryKey);
		super.updateInternal(primaryKey, entity);
		entity.setCompanyId(oldEntity.get().getCompanyId());
		return super.saveInternal(entity);
	}
	
	@Override
	public ContactPerson partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		
		var oldEntity = contactPersonRepository.findById(primaryKey);
		var entity = super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch));
		super.updateInternal(primaryKey, entity);
		entity.setCompanyId(oldEntity.get().getCompanyId());
		return super.saveInternal(entity);
	}
	
	@Override
	public Page<ContactPerson> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<ContactPerson> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
}
