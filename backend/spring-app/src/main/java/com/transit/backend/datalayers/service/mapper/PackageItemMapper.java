package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface PackageItemMapper extends AbstractMapper<PackageItem, PackageItemDTO> {
	
	@Named("dtoToDouble")
	public static double dtoToDouble(Double d) {
		if (d == null || Double.isNaN(d) || d == 0.0) {
			return Double.NaN;
		} else {
			return d;
		}
	}
	
	@Named("doubleToDTO")
	public static Double getComplexDouble(double d) {
		if (Double.isNaN(d) || d == 0.0) {
			return Double.NaN;
		}
		return d;
	}
	
	@Mapping(source = "packagePrice", target = "packagePrice", qualifiedByName = "dtoToDouble")
	PackageItem toEntity(PackageItemDTO dto);
	
	@Mapping(target = "packagePackageProperties.packageItem", ignore = true)
	@Mapping(source = "packagePrice", target = "packagePrice", qualifiedByName = "doubleToDTO")
	PackageItemDTO toDto(PackageItem entity);
	
	
}