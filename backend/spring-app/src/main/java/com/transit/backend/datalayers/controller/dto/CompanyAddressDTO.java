package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.helper.contraints.ZipCodeConstraint;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companyAddresses", itemRelation = "companyAddress")
@ZipCodeConstraint
public class CompanyAddressDTO extends AbstractDTO<CompanyAddressDTO> implements Comparable<CompanyAddressDTO> {
	@NotNull(groups = ValidationGroups.Post.class)
	@NotNull(groups = ValidationGroups.Put.class)
	@NotNull(groups = ValidationGroups.Patch.class)
	private UUID companyId;
	
	@NotBlank
	private String addressType;
	
	@NotNull
	@JsonSerialize(using = GeometrySerializer.class)
	@JsonDeserialize(using = GeometryDeserializer.class)
	private Point locationPoint;
	
	private String street;
	@NotBlank
	private String zip;
	@NotBlank
	private String city;
	
	private String state;
	@NotBlank
	private String country;
	@NotBlank
	private String isoCode;
	
	private String addressExtra;
	
	private String companyName;
	
	private String clientName;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyAddressDTO that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(companyId, that.companyId) && Objects.equal(addressType, that.addressType) && Objects.equal(locationPoint, that.locationPoint) && Objects.equal(street, that.street) && Objects.equal(zip, that.zip) && Objects.equal(city, that.city) && Objects.equal(state, that.state) && Objects.equal(country, that.country) && Objects.equal(isoCode, that.isoCode) && Objects.equal(addressExtra, that.addressExtra) && Objects.equal(companyName, that.companyName) && Objects.equal(clientName, that.clientName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), companyId, addressType, locationPoint, street, zip, city, state, country, isoCode, addressExtra, companyName, clientName);
	}
	
	@Override
	public int compareTo(@NotNull CompanyAddressDTO o) {
		return this.companyId.compareTo(o.companyId) + this.getId().compareTo(o.getId());
	}
}
