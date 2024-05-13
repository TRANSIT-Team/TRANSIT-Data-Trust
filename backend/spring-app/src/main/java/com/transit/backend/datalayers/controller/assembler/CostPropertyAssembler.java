package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CostCostPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class CostPropertyAssembler extends AbstractAssemblerWithoutLink<CostProperty, CostPropertyDTO, CostCostPropertyController> {
	@Autowired
	private CostPropertyMapper costPropertyMapper;
	
	
	public CostPropertyAssembler() {
		super(CostCostPropertyController.class, CostPropertyDTO.class);
	}
	
	@Override
	public CostPropertyDTO toModel(CostProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<CostProperty, CostPropertyDTO> getMapper() {
		return this.costPropertyMapper;
	}
	
	@Override
	public CollectionModel<CostPropertyDTO> toCollectionModel(Iterable<? extends CostProperty> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}