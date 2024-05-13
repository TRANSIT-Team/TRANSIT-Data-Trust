package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CostController;
import com.transit.backend.datalayers.controller.CostCostPropertyController;
import com.transit.backend.datalayers.controller.assembler.CostPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CostDTO;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CostCostPropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<CostProperty, CostPropertyDTO, Cost, CostDTO, CostController, CostCostPropertyController> {
	
	@Autowired
	CostPropertyAssembler costCostPropertyAssembler;
	
	public CostPropertyDTO toModel(CostProperty entity, UUID packageItemId, boolean backwardLink) {
		return super.toModel(entity, packageItemId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<CostProperty, CostPropertyDTO> getAssemblerSupport() {
		return this.costCostPropertyAssembler;
	}
	
	public CostPropertyDTO addLinks(CostPropertyDTO dto, UUID packageItemId, boolean backwardLink) {
		return super.addLinks(dto, packageItemId, backwardLink);
	}
	
	@Override
	public Class<CostCostPropertyController> getNestedControllerClazz() {
		return CostCostPropertyController.class;
	}
	
	@Override
	public Class<CostController> getRootControllerClazz() {
		return CostController.class;
	}
	
	@Override
	public Class<Cost> getDomainNameClazz() {
		return Cost.class;
	}
	
	public CollectionModel<CostPropertyDTO> toCollectionModel(Iterable<? extends CostProperty> entities, UUID packageItemId, boolean backwardLink) {
		return super.toCollectionModel(entities, packageItemId, backwardLink);
	}
}