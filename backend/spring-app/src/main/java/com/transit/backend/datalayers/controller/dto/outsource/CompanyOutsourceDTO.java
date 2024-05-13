package com.transit.backend.datalayers.controller.dto.outsource;

import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.util.SortedSet;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companies", itemRelation = "company")
public class CompanyOutsourceDTO extends AbstractPropertiesParentDTO<CompanyOutsourceDTO, CompanyPropertyDTO> {
	
	
	private String name;
	
	private SortedSet<CompanyAddressDTO> companyAddresses;
	
	private SortedSet<CompanyPropertyDTO> companyProperties;
	
	
	private CompanyDeliveryAreaDTO companyDeliveryArea;
	
	@Override
	public SortedSet<CompanyPropertyDTO> getProperties() {
		return this.companyProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CompanyPropertyDTO> properties) {
		this.setCompanyProperties(properties);
		
	}
}
