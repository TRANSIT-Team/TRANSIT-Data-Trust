package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CostDefaultPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.CostDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.CostDefaultProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostDefaultPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class CostDefaultPropertyAssembler extends AbstractAssembler<CostDefaultProperty, CostDefaultPropertyDTO, CostDefaultPropertyController> {
	@Autowired
	private CostDefaultPropertyMapper packagePropertyMapper;
	
	public CostDefaultPropertyAssembler() {
		super(CostDefaultPropertyController.class, CostDefaultPropertyDTO.class);
	}
	
	@Override
	public CostDefaultPropertyDTO toModel(CostDefaultProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<CostDefaultProperty, CostDefaultPropertyDTO> getMapper() {
		return this.packagePropertyMapper;
	}
	
	@Override
	public CollectionModel<CostDefaultPropertyDTO> toCollectionModel(Iterable<? extends CostDefaultProperty> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<CostDefaultPropertyController> getControllerClass() {
		return CostDefaultPropertyController.class;
	}
	
}