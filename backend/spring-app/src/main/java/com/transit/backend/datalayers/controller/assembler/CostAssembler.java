package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CostController;
import com.transit.backend.datalayers.controller.CostCostPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.CostCostPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CostDTO;
import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.CostProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CostAssembler extends AbstractAssemblerExtendProperties<Cost, CostDTO, CostProperty, CostPropertyDTO, CostCostPropertyController, CostController> {
	@Autowired
	private CostMapper mapper;
	
	@Autowired
	private CostCostPropertyAssemblerWrapper costcostPropertiesAssemblerWrapper;
	
	public CostAssembler() {
		super(CostController.class, CostDTO.class);
	}
	
	@Override
	public AbstractMapper<Cost, CostDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public Class<CostController> getControllerClass() {
		return CostController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CostProperty, CostPropertyDTO, Cost, CostDTO, CostController, CostCostPropertyController> getSubAssemblerWrapper() {
		return costcostPropertiesAssemblerWrapper;
	}
}
