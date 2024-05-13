package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.CustomerDTO;
import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.datalayers.domain.QCustomer;
import com.transit.backend.datalayers.repository.CompanyCustomerRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CompanyCustomerService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CustomerMapper;
import com.transit.backend.exeptions.exeption.ForbiddenException;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyCustomerServiceBean extends CrudServiceExtendAbstract<Customer, CustomerDTO> implements CompanyCustomerService {
	@Autowired
	private CompanyCustomerRepository customerRepository;
	@Autowired
	private CustomerMapper mapper;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Override
	public Customer create(UUID companyId, Customer entity) {
		entity.setCompanyId(companyId);
		return super.saveInternal(entity);
	}
	
	@Override
	public Customer update(UUID companyId, UUID customerId, Customer entity) {
		var oldEntity = customerRepository.findById(customerId);
		super.updateInternal(customerId, entity);
		entity.setCompanyId(oldEntity.get().getCompanyId());
		return super.saveInternal(entity);
	}
	
	@Override
	public Customer partialUpdate(UUID companyId, UUID customerId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var oldEntity = customerRepository.findById(customerId);
		var entity = super.checkviolationsInternal(customerId, super.partialUpdateInternal(customerId, patch));
		super.updateInternal(customerId, entity);
		entity.setCompanyId(oldEntity.get().getCompanyId());
		return super.saveInternal(entity);
	}
	
	@Override
	public Collection<Customer> read(UUID companyId, String query, FilterExtra FilterExtra) {
		return this.read(FilterExtra, companyId, query).getContent();
	}
	
	public Page<Customer> read(FilterExtra pageable, UUID companyId, String query) {
		if (query != null && !query.isBlank()) {
			query = "( " + query + " ) and companyId==" + userHelperFunctions.getCompanyId();
		} else {
			query = "companyId==" + userHelperFunctions.getCompanyId();
		}
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Customer> readOne(UUID companyId, UUID customerId) {
		var result = super.readOneInternal(customerId);
		if (result.isPresent()) {
			if (!result.get().getCompanyId().equals(companyId)) {
				throw new ForbiddenException();
			}
		}
		return result;
	}
	
	@Override
	public void delete(UUID companyId, UUID customerId) {
		super.saveInternal(super.deleteInternal(customerId));
	}
	
	@Override
	public AbstractRepository<Customer> getRepository() {
		return this.customerRepository;
	}
	
	@Override
	public AbstractMapper<Customer, CustomerDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public Class<Customer> getEntityClazz() {
		return Customer.class;
	}
	
	@Override
	public Class<CustomerDTO> getEntityDTOClazz() {
		return CustomerDTO.class;
	}
	
	@Override
	public Path<Customer> getQClazz() {
		return QCustomer.customer;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
}
