package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.PackageItemController;
import com.transit.backend.datalayers.controller.PackageItemPackageClassController;
import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.PackageItemPackageClassAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.PackageItemPackagePackagePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackageClassDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackageItemMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PackageItemAssembler extends AbstractAssemblerExtendProperties<PackageItem, PackageItemDTO, PackagePackageProperty, PackagePackagePropertyDTO, PackageItemPackagePackagePropertyController, PackageItemController> {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private PackageItemMapper packageItemMapper;
	@Autowired
	private PackageItemPackageClassAssemblerWrapper packageItemPackageClassAssembler;
	@Autowired
	private PackageItemPackagePackagePropertyAssemblerWrapper packageItemPackagePropertyAssembler;
	
	
	public PackageItemAssembler() {
		super(PackageItemController.class, PackageItemDTO.class);
	}
	
	@Override
	public PackageItemDTO toModel(PackageItem packageItem) {
		
		var dto = super.toModel(packageItem);
		dto.add(linkTo(methodOn(PackageItemPackagePackagePropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("packagePackageProperties"));
		if (packageItem.getPackageClass() != null) {
			dto.add(linkTo(methodOn(PackageItemPackageClassController.class).readOne(dto.getId(), packageItem.getPackageClass().getId())).withRel("packageClasses"));
		}
		dto.setPackageClass(toClassDTO(packageItem.getId(), packageItem.getPackageClass()));
		return dto;
	}
	
	@Override
	public AbstractMapper<PackageItem, PackageItemDTO> getMapper() {
		return this.packageItemMapper;
	}
	
	@Override
	public CollectionModel<PackageItemDTO> toCollectionModel(Iterable<? extends PackageItem> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<PackageItemController> getControllerClass() {
		return PackageItemController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackageItemPackagePackagePropertyController> getSubAssemblerWrapper() {
		return this.packageItemPackagePropertyAssembler;
	}
	
	private PackagePackageClassDTO toClassDTO(UUID packageItemId, PackageClass packageClass) {
		if (packageClass == null) {
			return null;
		} else {
			try {
				var dto = packageItemPackageClassAssembler.toModel(packageClass, packageItemId);
				return objectMapper.treeToValue(objectMapper.valueToTree(dto), PackagePackageClassDTO.class);
			} catch (JsonProcessingException ex) {
				throw new BadRequestException("Cannot map PackageClass Object in " + getClass().getSimpleName());
			}
		}
	}
	
	
}
