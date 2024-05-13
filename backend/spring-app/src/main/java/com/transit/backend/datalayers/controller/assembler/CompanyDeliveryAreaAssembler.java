package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyDeliveryAreaController;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.service.mapper.CompanyDeliveryAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CompanyDeliveryAreaAssembler extends RepresentationModelAssemblerSupport<CompanyDeliveryArea, CompanyDeliveryAreaDTO> {
	@Autowired
	private CompanyDeliveryAreaMapper companyDeliveryAreaMapper;
	
	public CompanyDeliveryAreaAssembler() {
		super(CompanyDeliveryAreaController.class, CompanyDeliveryAreaDTO.class);
	}
	
	@Override
	public CompanyDeliveryAreaDTO toModel(CompanyDeliveryArea entity) {
		
		return companyDeliveryAreaMapper.toDto(entity);
	}
	
	@Override
	public CollectionModel<CompanyDeliveryAreaDTO> toCollectionModel(Iterable<? extends CompanyDeliveryArea> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<CompanyDeliveryAreaController> getControllerClass() {
		return CompanyDeliveryAreaController.class;
	}
	
}