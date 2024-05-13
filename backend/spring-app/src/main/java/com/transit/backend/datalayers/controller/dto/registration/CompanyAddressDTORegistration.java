package com.transit.backend.datalayers.controller.dto.registration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.transit.backend.helper.contraints.ZipCodeConstraint;
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

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "companyAddresses", itemRelation = "companyAddress")
@ZipCodeConstraint
public class CompanyAddressDTORegistration implements Comparable<CompanyAddressDTORegistration> {
	
	
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
	public int compareTo(@org.jetbrains.annotations.NotNull CompanyAddressDTORegistration o) {
		return this.hashCode() - o.hashCode();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(addressType, locationPoint, street, zip, city, state, country, isoCode, addressExtra, companyName, clientName);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CompanyAddressDTORegistration that)) return false;
		return Objects.equal(addressType, that.addressType) && Objects.equal(locationPoint, that.locationPoint) && Objects.equal(street, that.street) && Objects.equal(zip, that.zip) && Objects.equal(city, that.city) && Objects.equal(state, that.state) && Objects.equal(country, that.country) && Objects.equal(isoCode, that.isoCode) && Objects.equal(addressExtra, that.addressExtra) && Objects.equal(companyName, that.companyName) && Objects.equal(clientName, that.clientName);
	}
}
