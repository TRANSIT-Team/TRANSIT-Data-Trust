package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyCompanyPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class CompanyPropertyAssembler extends AbstractAssemblerWithoutLink<CompanyProperty, CompanyPropertyDTO, CompanyCompanyPropertyController> {
	@Autowired
	private CompanyPropertyMapper companyPropertyMapper;
	
	
	public CompanyPropertyAssembler() {
		super(CompanyCompanyPropertyController.class, CompanyPropertyDTO.class);
	}
	
	@Override
	public CompanyPropertyDTO toModel(CompanyProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<CompanyProperty, CompanyPropertyDTO> getMapper() {
		return this.companyPropertyMapper;
	}
	
	@Override
	public CollectionModel<CompanyPropertyDTO> toCollectionModel(Iterable<? extends CompanyProperty> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}