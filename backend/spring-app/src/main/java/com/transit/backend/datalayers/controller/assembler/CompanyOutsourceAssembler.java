package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyCompanyAddressController;
import com.transit.backend.datalayers.controller.CompanyCompanyPropertyController;
import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyDeliveryAreaController;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyDeliveryAreaAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.outsource.CompanyOutsourceDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.mapper.CompanyOutsourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompanyOutsourceAssembler extends RepresentationModelAssemblerSupport<Company, CompanyOutsourceDTO> {
	@Autowired
	private CompanyOutsourceMapper companyOutsourceMapper;
	
	@Autowired
	private CompanyCompanyPropertyAssemblerWrapper companyCompanyPropertyAssemblerWrapper;
	
	@Autowired
	private CompanyCompanyAddressAssemblerWrapper companyCompanyAddressAssemblerWrapper;
	
	@Autowired
	private CompanyCompanyDeliveryAreaAssemblerWrapper companyCompanyDeliveryAreaAssemblerWrapper;
	
	public CompanyOutsourceAssembler() {
		super(CompanyController.class, CompanyOutsourceDTO.class);
	}
	
	@Override
	public CompanyOutsourceDTO toModel(Company entity) {
		var result = companyOutsourceMapper
				.toDto(entity)
				.add(linkTo(methodOn(CompanyController.class).readOne(entity.getId())).withSelfRel()).
				add(linkTo(methodOn(CompanyCompanyPropertyController.class).read(entity.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("companyProperties"))
				.add(linkTo(methodOn(CompanyCompanyAddressController.class).read(entity.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("companyAddresses"));
		if (entity.getCompanyDeliveryArea() != null) {
			result.add(linkTo(methodOn(CompanyDeliveryAreaController.class).readOne(entity.getId(), entity.getCompanyDeliveryArea().getId())).withRel("deliveryArea"));
		}
		result.setCompanyProperties(this.toPropertyDTO(entity.getId(), entity.getProperties()));
		result.setCompanyAddresses(this.toAddressDTO(entity.getId(), entity.getCompanyAddresses()));
		if (entity.getCompanyDeliveryArea() != null) {
			result.setCompanyDeliveryArea(companyCompanyDeliveryAreaAssemblerWrapper.toModel(entity.getCompanyDeliveryArea(), entity.getId(), false));
		}
		return result;
	}
	
	private SortedSet<CompanyPropertyDTO> toPropertyDTO(UUID rootId, SortedSet<CompanyProperty> properties) {
		if (properties == null || properties.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(companyCompanyPropertyAssemblerWrapper.toCollectionModel(properties, rootId, false).getContent());
		}
	}
	
	private SortedSet<CompanyAddressDTO> toAddressDTO(UUID rootId, SortedSet<CompanyAddress> properties) {
		if (properties == null || properties.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(companyCompanyAddressAssemblerWrapper.toCollectionModel(properties, rootId, false).getContent());
		}
	}
	
	@Override
	public CollectionModel<CompanyOutsourceDTO> toCollectionModel(Iterable<? extends Company> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}
