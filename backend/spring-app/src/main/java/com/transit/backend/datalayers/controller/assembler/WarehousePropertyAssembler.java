package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.WarehouseWarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehousePropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarehousePropertyAssembler extends AbstractAssemblerWithoutLink<WarehouseProperty, WarehousePropertyDTO, WarehouseWarehousePropertyController> {
	
	@Autowired
	private WarehousePropertyMapper warehousePropertyMapper;
	
	public WarehousePropertyAssembler() {
		super(WarehouseWarehousePropertyController.class, WarehousePropertyDTO.class);
	}
	
	@Override
	public AbstractMapper<WarehouseProperty, WarehousePropertyDTO> getMapper() {
		return this.warehousePropertyMapper;
	}
}
