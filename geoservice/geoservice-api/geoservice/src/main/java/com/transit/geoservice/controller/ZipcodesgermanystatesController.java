package com.transit.geoservice.controller;

import com.transit.geoservice.domain.Zipcodesgermanystates;
import com.transit.geoservice.service.ZipcodesgermanystatesInterface;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping("/zipstates")
public class ZipcodesgermanystatesController {

@Autowired
private ZipcodesgermanystatesInterface zipcodesgermanystatesInterface;


    @GetMapping( path = "/state/{name}")
    public ResponseEntity<List<Zipcodesgermanystates>> readOneState(@PathVariable("name") String name){

        var entity = zipcodesgermanystatesInterface.getZipcodesgermanystatesByState(name);
        if(entity.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);

    }


    @GetMapping( path = "/zip/{zip}")
    public ResponseEntity<Zipcodesgermanystates> readOnePLZ(@PathVariable("zip") String zip){

        var entity = zipcodesgermanystatesInterface.getZipcodesgermanystatesByZip(zip);
        if(entity.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(entity.get(), HttpStatus.OK);

    }

    @GetMapping( path = "/")
    public ResponseEntity<List<Zipcodesgermanystates>> readAll(){

        var entity = zipcodesgermanystatesInterface.getZipcodesgermanystatesByAll();


        if(entity.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }


}
