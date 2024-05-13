package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.QAddress;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.WarehouseRepository;
import com.transit.backend.datalayers.service.WarehouseAddressService;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WarehouseAddressServiceBean implements WarehouseAddressService {
	
	
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	AddressMapper mapper;
	@Autowired
	ObjectMapper objectMapper;
	@Inject
	Validator validator;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	
	@Override
	public Address create(UUID warehouseId, Address entity) {
		
		
		return warehouseRepository
				.findById(warehouseId)
				.map(
						p -> {
							if (p.getAddress() == null) {
								var warehouseAddress = addressRepository.saveAndFlush(entity);
								p.setAddress(warehouseAddress);
								warehouseRepository.saveAndFlush(p);
								return warehouseAddress;
							} else {
								throw new UnprocessableEntityExeption("Address for Warehouse already exists.");
							}
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Warehouse.class.getSimpleName(), warehouseId));
	}
	
	@Override
	public Address update(UUID warehouseId, UUID addressId, Address entity) {
		return warehouseRepository
				.findByIdAndDeleted(warehouseId, false)
				.map(
						p -> {
							var warehouseAddress = p.getAddress();
							putPatchValidatorProperties.validator(Address.class, rightsService.getAccess(addressId), p.getAddress(), entity);
							entity.setId(addressId);
							entity.setCreateDate(addressRepository.getReferenceById(warehouseAddress.getId()).getCreateDate());
							entity.setCreatedBy(addressRepository.getReferenceById(warehouseAddress.getId()).getCreatedBy());
							return addressRepository.saveAndFlush(entity);
						})
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Warehouse.class.getSimpleName(), warehouseId));
		
	}
	
	@Override
	public Address partialUpdate(UUID warehouseId, UUID addressId, JsonMergePatch patch) {
		return warehouseRepository
				.findByIdAndDeleted(warehouseId, false)
				.map(
						p -> {
							var warehouseAddress = p.getAddress();
							try {
								AddressDTO warehouseAddressDTO = mapper.toDto(warehouseAddress);
								JsonNode original = objectMapper.valueToTree(warehouseAddressDTO);
								JsonNode patched = patch.apply(original);
								warehouseAddressDTO = objectMapper.treeToValue(patched, AddressDTO.class);
								
								Set<ConstraintViolation<AddressDTO>> violations = validator.validate(warehouseAddressDTO, ValidationGroups.Patch.class);
								if (violations.isEmpty()) {
									var warehouseAddressPatched = mapper.toEntity(warehouseAddressDTO);
									putPatchValidatorProperties.validator(Address.class, rightsService.getAccess(addressId), p.getAddress(), warehouseAddressPatched);
									warehouseAddressPatched.setId(addressId);
									return addressRepository.saveAndFlush(warehouseAddressPatched);
								} else {
									throw new ConstraintViolationException(
											new HashSet<>(violations));
								}
							} catch (JsonPatchException | JsonProcessingException e) {
								throw new UnprocessableEntityExeption(e.getMessage());
							}
						})
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Warehouse.class.getSimpleName(), warehouseId));
		
	}
	
	@Override
	public Collection<Address> read(UUID warehouseId, String query, FilterExtra pageParams) {
		if (query.trim().isBlank()) {
			query += "warehouse.id==" + warehouseId;
		} else {
			query = "( " + query + " ) and warehouse.id==" + warehouseId;
		}
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QAddress.address);
		warehouseRepository.findById(warehouseId).orElseThrow(() -> new NoSuchElementFoundException(Warehouse.class.getSimpleName(), warehouseId));
		Pageable pageable;
		if (pageParams.isUseOtherParameters()) {
			pageable = new OffsetBasedPageRequest(pageParams.getTake(), pageParams.getSkip(), true);
			return new TreeSet<>(addressRepository.findAll(spec, pageable).toList());
		} else {
			return StreamSupport
					.stream(addressRepository.findAll(spec).spliterator(), false)
					.collect(Collectors.toCollection(TreeSet::new));
		}
	}
	
	@Override
	public Optional<Address> readOne(UUID warehouseId, UUID addressId) {
		
		return warehouseRepository.findById(warehouseId).map(Warehouse::getAddress);
	}
	
	
	@Override
	public void delete(UUID warehouseId, UUID addressId) {
		warehouseRepository
				.findById(warehouseId)
				.map(
						p -> {
							
							var warehouseAddress = p.getAddress();
							warehouseAddress.setDeleted(true);
							addressRepository.saveAndFlush(warehouseAddress);
							return Optional.empty();
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Warehouse.class.getSimpleName(), warehouseId));
	}
}
	