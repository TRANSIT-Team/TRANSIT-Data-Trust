package com.transit.geoservice.domain;


import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "zipcodesgermanypolygons")
public class Zipcodesgermanypolygon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogc_fid", nullable = false)
    private Integer id;
    
    @Column(name = "plz")
    private String plz;
    
    @Column(name = "note")
    private String note;

    @Column(name = "einwohner")
    private Integer einwohner;

    @Column(name = "qkm")
    private Double qkm;
    
    
    
    
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
  @Column(name = "wkb_geometry", columnDefinition = "geometry")
  private Geometry wkbGeometry;

}