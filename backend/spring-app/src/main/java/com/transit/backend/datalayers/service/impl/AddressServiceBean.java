package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.QAddress;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.AddressService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;


@Service
public class AddressServiceBean extends CrudServiceExtendAbstract<Address, AddressDTO> implements AddressService {
	
	
	@Autowired
	private AddressRepository repository;
	
	@Autowired
	private AddressMapper mapper;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public AbstractRepository<Address> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<Address, AddressDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<Address> getEntityClazz() {
		return Address.class;
	}
	
	@Override
	public Class<AddressDTO> getEntityDTOClazz() {
		return AddressDTO.class;
	}
	
	@Override
	public Path<Address> getQClazz() {
		return QAddress.address;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public Address create(Address entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public Address update(UUID primaryKey, Address entity) {
		
		this.isUpdatable(primaryKey);
		entity = super.updateInternal(primaryKey, entity);
		entity = setBooleanShow(primaryKey, entity);
		return super.saveInternal(entity);
	}
	
	@Override
	public Address partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		this.isUpdatable(primaryKey);
		var entity = super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch));
		entity = setBooleanShow(primaryKey, entity);
		return super.saveInternal(entity);
	}
	
	@Override
	public Page<Address> read(FilterExtra pageable, String query) {
		if (query.isBlank()) {
			query = "showOverviewFilter=='SHOW'";
		} else {
			query = "( " + query + " ) and showOverviewFilter=='SHOW'";
		}
		
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Address> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	private void isUpdatable(UUID primaryKey) {
		if (orderRepository.existsOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(primaryKey, primaryKey, primaryKey)) {
			throw new UnprocessableEntityExeption("Address cannot changed, because it is already used in an Order.");
		}
	}
	
	public Address setBooleanShow(UUID primaryKey, Address address) {
		var oldEntity = repository.findById(primaryKey);
		address.setShowOverviewFilter(oldEntity.get().getShowOverviewFilter());
		return address;
	}
	
}