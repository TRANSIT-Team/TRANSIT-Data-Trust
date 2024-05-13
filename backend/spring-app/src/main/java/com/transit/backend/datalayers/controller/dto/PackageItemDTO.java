package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.transit.backend.config.jackson.JacksonDeserializeDouble;
import com.transit.backend.config.jackson.JacksonSerializeDouble;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.SortedSet;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "packageItems", itemRelation = "packageItem")
public class PackageItemDTO extends AbstractPropertiesParentDTO<PackageItemDTO, PackagePackagePropertyDTO> {
	
	
	@JsonProperty("packagePackageProperties")
	@Valid
	private SortedSet<PackagePackagePropertyDTO> packagePackageProperties;
	@Valid
	@NotNull
	private PackagePackageClassDTO packageClass;
	
	
	private double weightKg;
	
	private double heightCm;
	private double widthCm;
	private double deepCm;
	
	private boolean frost;
	
	private boolean explosive;
	
	
	private String comment;
	
	@JsonDeserialize(using = JacksonDeserializeDouble.class)
	@JsonSerialize(using = JacksonSerializeDouble.class)
	
	private Double packagePrice;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PackageItemDTO that)) return false;
		if (!super.equals(o)) return false;
		return Double.compare(that.weightKg, weightKg) == 0 && Double.compare(that.heightCm, heightCm) == 0 && Double.compare(that.widthCm, widthCm) == 0 && Double.compare(that.deepCm, deepCm) == 0 && Objects.equals(packagePackageProperties, that.packagePackageProperties) && Objects.equals(packageClass, that.packageClass);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), packagePackageProperties, packageClass, weightKg, heightCm, widthCm, deepCm);
	}
	
	@Override
	public SortedSet<PackagePackagePropertyDTO> getProperties() {
		return this.packagePackageProperties;
	}
	
	@Override
	public void setProperties(SortedSet<PackagePackagePropertyDTO> properties) {
		this.setPackagePackageProperties(properties);
	}
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (!Double.isNaN(packagePrice) && packagePrice == 0.0) {
			packagePrice = Double.NaN;
		}
	}
}
