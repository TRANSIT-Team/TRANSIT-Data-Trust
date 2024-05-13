package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.IdentifierDTO;
import com.transit.backend.datalayers.controller.dto.OrderOverviewDTO;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.PackageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderOverviewMapper extends AbstractMapper<Order, OrderOverviewDTO> {
	
	@Named("dtoToOrder")
	public static Order dtoToSuborder(IdentifierDTO dto) {
		if (dto == null) {
			return null;
		}
		var entity = new Order();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToPackageItem")
	public static PackageItem dtoToPackageItem(PackageItemDTO dto) {
		var entity = new PackageItem();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("orderToDTO")
	public static IdentifierDTO orderToDTO(Order dto) {
		if (dto == null) {
			return null;
		}
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Mapping(source = "addressFrom", target = "addressFrom")
	@Mapping(source = "addressTo", target = "addressTo")
	@Mapping(source = "addressBilling", target = "addressBilling")
	
	@Mapping(source = "companyId.id", target = "company.id")
	
	@Mapping(source = "orderStatus", target = "orderStatus")
	
	@Mapping(source = "packageItems", target = "packageItems")
	
	@Mapping(source = "parentOrderId", target = "parentOrder", qualifiedByName = "dtoToOrder")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "packagesPrice", target = "packagesPrice")
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "outsourceCost", target = "outsourceCost")
	@Mapping(source = "customerId.id", target = "customerId")
	@Mapping(source = "id", target = "id")
	Order toEntity(OrderOverviewDTO dto);
	
	@Mapping(target = "addressFrom", source = "addressFrom")
	@Mapping(target = "addressTo", source = "addressTo")
	@Mapping(target = "addressBilling", source = "addressBilling")
	
	@Mapping(target = "companyId.id", source = "company.id")
	
	@Mapping(target = "orderStatus", source = "orderStatus")
	
	@Mapping(target = "packageItems", source = "packageItems")
	
	@Mapping(target = "parentOrderId", source = "parentOrder", qualifiedByName = "orderToDTO")
	
	@Mapping(source = "price", target = "price")
	@Mapping(source = "packagesPrice", target = "packagesPrice")
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "outsourceCost", target = "outsourceCost")
	@Mapping(source = "customerId", target = "customerId.id")
	@Mapping(source = "id", target = "shortId")
	@Mapping(source = "id", target = "id")
	OrderOverviewDTO toDto(Order entity);
	
	
}